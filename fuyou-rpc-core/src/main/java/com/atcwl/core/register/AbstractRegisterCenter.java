package com.atcwl.core.register;

import com.alibaba.fastjson2.JSON;
import com.atcwl.common.cache.SimpleRpcServiceCache;
import com.atcwl.common.config.LocalAddressInfo;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.constrant.CommonConstant;
import com.atcwl.common.constrant.enums.LoadBalanceRule;
import com.atcwl.common.interfaces.FuyouRpcLoadBalancer;
import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.common.interfaces.impl.RegisterInfo;
import com.atcwl.common.network.HookEntity;
import com.atcwl.common.spi.ExtensionLoader;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册中心公共抽象类，提供了注册中心的基本功能方法
 *
 * @author: WuChengXing
 * @create: 2022-04-21 18:44
 **/
public abstract class AbstractRegisterCenter implements RegisterCenter {

    @Override
    public void init(SimpleRpcUrl url) {

    }

    /**
     * 向注册中心注册对应服务，当选定好注册中心的类型后，注册行为是类似的，可以采用模板方法的设计模式
     * @param request
     * @return
     */
    @Override
    public String register(RegisterInfo request) {
        //拼接服务接口标识Key：默认格式：RPC框架自带前缀+接口名称+别名，以下划线连接
        String key = CommonConstant.RPC_SERVICE_PREFIX + "_" + request.getInterfaceName() + "_" +
                request.getAlias();
        //拼接服务Key，这个key代表了一个服务节点
        String fieldKey = request.getHost() + "_" + request.getPort();
        String value = JSON.toJSONString(request);
        //拼接应用标识，一个应用服务可以有多个节点
        String appName = CommonConstant.RPC_APP_PREFIX + "_" + request.getApplicationName();
        //首先构建应用名和接口标识关系，这里会以appName为key，接口标识key为value构建关系
        buildAppName(appName, key);
        //前面接口key形成了一个哈希表，我们现在将在某一个服务节点调用该接口的结果存入到该哈希表
        //简述关系：应用服务——>接口——>服务节点
        buildDataAndSave(key, fieldKey, value);
        //返回key，后续可以根据该key找到对应服务节点列表
        return key;
    }

    /**
     * 将三个字段构建然后存入：
     * --- 127.0.0.1_41201 --- {"alias":"xxx","host":"127.0.0.1","port":41201,"serializer":"serializer","weights":20}
     * -
     * com.simple.rpc.HelloService_helloService --- 127.0.0.1_41202 --- {"alias":"xxx","host":"127.0.0.1","port":41202,"serializer":"serializer","weights":30}
     * -
     * --- 127.0.0.1_41203 --- {"alias":"xxx","host":"127.0.0.1","port":41203,"serializer":"serializer","weights":50}
     *
     * @param key
     * @param hostPort
     * @param request
     * @return
     */
    protected abstract Boolean buildDataAndSave(String key, String hostPort, String request);

    /**
     * 构建appName - rpcService相关的数据
     *  这两个构建方法的具体实现取决于注册中心的类型，因此交由子类实现
     * @param appName
     * @param rpcService
     * @return
     */
    protected abstract Boolean buildAppName(String appName, String rpcService);

    /**
     * 远程调用时，通过get方法选择一个合适的节点返回
     * @param request
     * @return
     */
    @Override
    public String get(RegisterInfo request) {
        //先拿到接口标识key
        String key = CommonConstant.RPC_SERVICE_PREFIX + "_" + request.getInterfaceName() + "_" +
                request.getAlias();
        //根据接口标识key获取到服务节点信息
        Map<String, String> stringStringMap = getLoadBalanceData(key);
        // 过滤已下线的注册信息，这里会将那些真正下线的服务从注册中心中移除
        filterNotHealth(stringStringMap);
        //选定负载均衡规则
        String rule = Objects.isNull(request.getLoadBalanceRule()) ? LoadBalanceRule.ROUND.getName() : request.getLoadBalanceRule();
        //根据负载均衡规则选择一个节点url并返回
        return ExtensionLoader.getLoader(FuyouRpcLoadBalancer.class).getExtension(rule).loadBalance(stringStringMap);
    }

    /**
     * 数据格式：{"127.0.0.1_41200" : "{"requestId: 1"}"}
     * 描述：通过 key（com.simple.rpc.AService_aService）获取的到 下面的map格式
     * map格式：前面的key以 host + "_" + port 组成；后面是对应的request信息的json格式
     * 获取对应接口的服务节点映射信息
     * @param key
     * @return
     */
    protected abstract Map<String, String> getLoadBalanceData(String key);

    /**
     * 解绑服务，即将服务注册信息从注册中心移除
     * @param hookEntity
     * @return
     */
    @Override
    public Boolean unregister(HookEntity hookEntity) {
        return null;
    }

    /**
     * 服务下线功能
     * 该方法可以将注册中心的某个服务节点下线，从而不让客户端调用到它
     * 在服务端出现问题时服务端可以调用该方法来完成预下线操作
     * @return
     */
    @Override
    public Boolean offline() {
        long start = System.currentTimeMillis();
        //得到本地服务的地址，其实是在服务节点本地调用，所以用本地地址即可
        String machine = LocalAddressInfo.LOCAL_HOST + "_" +  LocalAddressInfo.PORT;
        //从Service缓存中得到该服务所有接口的名称，用于组装key，然后进行操作
        List<String> serviceNames = SimpleRpcServiceCache.allKeys();
        //根据之前的接口名称到注册中心中获取到和该服务节点相关的注册信息
        List<String> multiKeyValue = this.getMultiKeyValue(serviceNames, machine);
        //更新和该服务节点相关的注册信息，也就是在某个服务下线后，更新远程注册表，并推送给客户端
        Boolean updateHealth = updateHealth(multiKeyValue, "0");
        //SimpleRpcLog.warn("预下线操作，状态：【{}】, 耗时：【{}】", updateHealth, System.currentTimeMillis() - start);
        return updateHealth;
    }

    /**
     * 更新服务节点注册信息
     * @param multiKeyValues
     * @param health
     * @return
     */
    private Boolean updateHealth(List<String> multiKeyValues, String health) {
        AtomicReference<Integer> updateNum = new AtomicReference<>(0);
        multiKeyValues.forEach(s -> {
            //对于注册信息，构建成RegisterInfo后，设置其health信息，并重新注册到注册表中覆盖原有信息即可
            RegisterInfo registerInfo = JSON.parseObject(s, RegisterInfo.class);
            registerInfo.setHealth(health);
            updateNum.getAndSet(updateNum.get() + 1);
            this.register(registerInfo);
        });
        //更新数量与接口数量相等时，此服务预下线才算成功，只要仍有一个接口调用信息残留，就不算完全下线
        return multiKeyValues.size() == updateNum.get();
    }

    /**
     * hmget命令去获取对应的值
     * 获取该服务节点所有接口下该节点对应的request信息
     * @param keys
     * @param machine
     * @return
     */
    protected abstract List<String> getMultiKeyValue(List<String> keys, String machine);

    @Override
    public Boolean online() {
        return null;
    }

    @Override
    public Boolean checkHealth() {
        return null;
    }

    /**
     * 过滤已经下线的服务
     * 前面有一个预下线的方法，预下线时，该服务节点所有接口的request的health都会被设置为0
     * 等到过滤下线服务时，就会将这些预下线的服务“真正下线”
     * @param registerInfos
     */
    @Override
    public void filterNotHealth(Map<String, String> registerInfos) {
        registerInfos.forEach((k, v) -> {
            String s = registerInfos.get(k);
            RegisterInfo registerInfo = JSON.parseObject(s, RegisterInfo.class);
            // 过滤掉已经下线的服务
            if ("0".equals(registerInfo.getHealth())) {
                registerInfos.remove(k);
            }
        });
    }
}
