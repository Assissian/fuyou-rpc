package com.atcwl.common.cache;

import com.atcwl.common.exception.SimpleRpcBaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC服务缓存，这里其实就是本地注册表
 * 与之前不同的是，这里我们是直接存储了对应接口实现类的对象，而不再是实现类类型了
 * 这种方式的好处是开箱即用，并且不同请求处理可以复用同一个对象，节省内存空间
 * 这与Spring的单例bean是类似的
 * @Author cwl
 * @date
 * @apiNote
 */
public class SimpleRpcServiceCache {
    //缓存对应实现类对象
    private final static Map<String, Object> SERVICE_CACHE = new ConcurrentHashMap<>();

    public static void addService(String name, Object bean) {
        SERVICE_CACHE.put(name, bean);
    }

    public static Object getService(String name) {
        Object service = SERVICE_CACHE.get(name);
        if (service == null) {
            throw new SimpleRpcBaseException("rpcService not found." + name);
        }
        return service;
    }

    public static List<String> allKeys() {
        return new ArrayList<String>(SERVICE_CACHE.keySet());
    }
}
