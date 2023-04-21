package com.atcwl.core.reflect;

import com.atcwl.common.config.CommonConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理类，用于产生客户端对应的接口代理
 * @Author cwl
 * @date
 * @apiNote
 */
public class RpcProxy {
    public static <T> T getServiceImpl(Class<T> inter, CommonConfig config) {
        InvocationHandler handler = new FuyouRpcInvocationHandler(config);
        ClassLoader classLoader = RpcProxy.class.getClassLoader();
        T invoke = (T) Proxy.newProxyInstance(classLoader, new Class[]{inter}, handler);
        return invoke;
    }
}
