package com.atcwl.core.reflect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.atcwl.common.cache.ApplicationCache;
import com.atcwl.common.config.*;
import com.atcwl.common.constrant.JavaKeywordConstant;
import com.atcwl.common.exception.SimpleRpcBaseException;
import com.atcwl.common.exception.network.NettyInitException;
import com.atcwl.common.exception.network.NettyInvokeException;
import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.common.interfaces.impl.RegisterInfo;
import com.atcwl.core.net.cache.CacheUtil;
import com.atcwl.core.net.cache.ConnectCache;
import com.atcwl.core.net.client.FuyouRpcClient;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.reflect.invoke.MultiInvoker;
import com.atcwl.core.register.RegisterCenterFactory;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class FuyouRpcInvocationHandler implements InvocationHandler {
    /**
     * 全局配置，其中包含了：用户自定义配置，基础配置，注册中心配置
     */
    private CommonConfig config;

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    ExecutorService invokeThreadPool = Executors.newFixedThreadPool(10);

    public FuyouRpcInvocationHandler() {
    }

    public FuyouRpcInvocationHandler(CommonConfig config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建连接建立请求
        Request request = buildRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        //将客户端与服务端进行连接
        connect(request);
        //建立连接成功后，获取后续服务调用相关信息
        String methodName = method.getName();
        Class[] parameterTypes = method.getParameterTypes();
        // 排除Object的方法调用
        if (JavaKeywordConstant.TO_STRING.equals(methodName) && parameterTypes.length == 0) {
            return this.toString();
        } else if (JavaKeywordConstant.HASHCODE.equals(methodName) && parameterTypes.length == 0) {
            return this.hashCode();
        } else if (JavaKeywordConstant.EQUALS.equals(methodName) && parameterTypes.length == 1) {
            return this.equals(args[0]);
        }
        //接下来需要正式发起服务调用
        //构建服务调用请求
        Request service = new Request();
        ConsumerConfig consumerConfig = config.getConsumerConfig();
        BaseConfig baseConfig = config.getBaseConfig();
        RegistryConfig registryConfig = config.getRegistryConfig();
        buildSendRequest(args, request, methodName, parameterTypes, service, consumerConfig, baseConfig);

        //发起服务调用，并获得响应结果
        Response response = waitResponse(service, baseConfig.getFaultTolerantType());
        Object exceptionInfo = response.getExceptionInfo();
        if (!Objects.isNull(exceptionInfo)) {
            String s = exceptionInfo.toString();
            if (StrUtil.isNotBlank(s)) {
                throw new NettyInvokeException("remote invoke fail!");
            }
        }
        return response.getData();
    }

    private Response waitResponse(Request service, String faultTolerantType) {
        // 容错机制，服务调用需要提供相应的容错机制，否则当服务调用出现问题时会给用户带来不好体验
        //具体调用过程交给Invoker执行，在Invoker中提供相应容错机制
        MultiInvoker multiInvoker = new MultiInvoker(service, faultTolerantType);
        //异步调用
        invokeThreadPool.submit(multiInvoker);
        Response response = null;
        //等待响应，若获取不到响应，则根据情况进行重试
        int tryNum = Objects.isNull(service.getRetryNum()) || service.getRetryNum() <= 0 ? 100 : service.getRetryNum();
        for (int i = 0; i < tryNum; i++) {
            if (null != response) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            response = multiInvoker.getResponse();
        }
        if (null == response) {
            throw new NettyInitException("服务端调用失败");
        }
        return response;
    }

    private void buildSendRequest(Object[] args, Request request, String methodName, Class[] parameterTypes, Request service, ConsumerConfig consumerConfig, BaseConfig baseConfig) {
        //设置参数，这里是构建服务调用请求了，连接已经建立，所以设置一些必要参数即可
        service.setMethodName(methodName);
        service.setParameterTypes(parameterTypes);
        service.setParameters(args);
        service.setBeanName(consumerConfig.getBeanName());
        service.setInterfaceName(consumerConfig.getInterfaceName());
        service.setChannel(Objects.requireNonNull(ConnectCache.getChannelFuture(request)).channel());
        service.setAlias(consumerConfig.getAlias());
        service.setSerializer(baseConfig.getSerializer());
        service.setRegister(baseConfig.getRegister());
        service.setCompressor(baseConfig.getCompressor());
        service.setTimeout(baseConfig.getTimeout());
    }

    /**
     * 将客户端连接至服务端，以便后续服务调用
     *
     * @param request
     */
    private void connect(Request request) {
        // 从channel缓存中获取channel
        ChannelFuture channelFuture = ConnectCache.getChannelFuture(request);
        //如果能够直接找到对应channel，那么说明连接已经建立，不能重复建立连接
        if (channelFuture != null && channelFuture.channel().isOpen()) {
            return;
        }
        //此时需要建立连接，为了避免并发建立多个连接的问题，这里加锁来保证连接建立的安全
        synchronized (this) {
            //双端检查，保证连接唯一
            if (channelFuture != null && channelFuture.channel().isOpen()) {
                return;
            }
            //删除连接，该方法会将所有和目的服务地址相关的连接从连接缓存中移除，然后建立新的连接
            CacheUtil.deleteConnect(channelFuture);
            channelFuture = ConnectCache.getChannelFuture(request);
            //获取通信channel，以客户端的身份建立连接
            FuyouRpcClient clientSocket = new FuyouRpcClient(request);
            //连接的具体建立过程交给子线程完成
            executorService.submit(clientSocket);
            //建立连接过程可能失败，因此这里设定了重试次数，重试次数由用户指定
            int tryNum = Objects.isNull(request.getRetryNum()) || request.getRetryNum() <= 0 ? 100 : request.getRetryNum();
            for (int i = 0; i < tryNum; i++) {
                //在连接还未建立成功时，循环重试建立连接，每次间隔一段时间
                if (null != channelFuture) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //若能成功建立连接，那么退出循环
                channelFuture = clientSocket.getChannelFuture();
            }
        }
        if (null == channelFuture) {
            throw new NettyInitException("客户端未连接上服务端，考虑增加重试次数");
        }
        //成功建立连接后，设置request的连接，后续要使用
        request.setChannelFuture(channelFuture);
        //建立连接后，需要保存连接缓存，后续复用
        ConnectCache.saveChannelFuture(request);
    }

    /**
     * 构建客户端与服务端建立连接的请求
     *
     * @return
     */
    private Request buildRequest() {
        ConsumerConfig consumerConfig = config.getConsumerConfig();
        RegistryConfig registryConfig = config.getRegistryConfig();
        BaseConfig baseConfig = config.getBaseConfig();
        //构建请求参数
        //此次请求主要向注册中心请求对应服务地址，同时将客户端自身注册到服务中心
        Request request = new Request();
        //设置请求的服务名
        request.setApplicationName(ApplicationCache.APPLICATION_NAME);
        //请求调用的接口名
        request.setInterfaceName(consumerConfig.getInterfaceName());
        //别名，主要用于构建查找对应接口实现类对象的Key
        request.setAlias(consumerConfig.getAlias());
        //指定负载均衡规则，这里我们是直接在连接建立请求中指定了后续访问服务时使用的负载均衡规则，不过对外是可配置的
        request.setLoadBalanceRule(baseConfig.getLoadBalanceRule());
        //根据用户配置的注册中心信息找到对应的注册中心，转换为URL信息
        SimpleRpcUrl simpleRpcUrl = SimpleRpcUrl.toSimpleRpcUrl(registryConfig);
        //加载对应类型的注册中心
        RegisterCenter registerCenter = RegisterCenterFactory.create(simpleRpcUrl.getType());
        //registerCenter==null说明没有找到符合要求的注册中心（可能注册中心未启动）
        if (Objects.isNull(registerCenter)) {
            throw new SimpleRpcBaseException("注册中心未初始化");
        }
        //将请求信息转化为注册信息，根据注册信息到注册中心获取对应服务地址信息
        String infoStr = registerCenter.get(Request.request2Register(request));
        //若找不到对应服务地址，则建立连接失败
        if (Objects.isNull(infoStr)) {
            return null;
        }
        //这里我们从注册中心中拿到了对应服务的URL信息，此时可以构建请求，并建立连接了
        RegisterInfo registerInfo = JSON.parseObject(infoStr, RegisterInfo.class);
        //这里根据返回的注册信息构建请求，之前的请求是向注册中心发的请求，这里才是真正与服务端建立连接的请求
        Request returnRequest = Request.register2Request(registerInfo);
        //补充请求的心跳间隔时间和最大重试次数
        returnRequest.setBeatIntervalTime(baseConfig.getBeatIntervalTime());
        returnRequest.setRetryNum(baseConfig.getRetryNum());
        return request;
    }
}