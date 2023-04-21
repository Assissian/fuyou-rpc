package com.atcwl.core.loadbalance;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.atcwl.common.interfaces.FuyouRpcLoadBalancer;
import com.atcwl.common.interfaces.impl.LoadBalanceParam;
import com.atcwl.core.net.message.Request;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 负载均衡抽象类
 *
 * @author: WuChengXing
 * @create: 2022-05-06 16:48
 **/
public abstract class AbstractLoadBalance implements FuyouRpcLoadBalancer {
    /**
     * 这里的services是之前存入到注册中心的服务节点URL： request字符串的一个hash表
     * @param services
     * @return
     */
    @Override
    public String loadBalance(Map<String, String> services) {
        if (CollectionUtil.isEmpty(services)) {
            return null;
        }
        Map<String, LoadBalanceParam> selectMap = new ConcurrentHashMap<>(4);
        Set<String> urls = services.keySet();
        for (String url : urls) {
            Request request = JSON.parseObject(services.get(url), Request.class);
            LoadBalanceParam param = new LoadBalanceParam();
            param.setWeights(request.getWeights());
            selectMap.put(url, param);
        }
        //这里是根据具体负载均衡规则选择一个URL返回
        String selectUrl = select(selectMap);
        return services.get(selectUrl);
    }

    /**
     * 实际的负载算法
     *  实际负载算法有多种，因此具体实现交由子类实现
     * @param urls
     * @return
     */
    public abstract String select(Map<String, LoadBalanceParam> urls);
}
