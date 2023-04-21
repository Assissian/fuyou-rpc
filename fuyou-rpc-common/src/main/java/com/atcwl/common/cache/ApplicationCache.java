package com.atcwl.common.cache;

import com.atcwl.common.cache.entity.ApplicationEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:    应用缓存，主要用于缓存指定服务应用相关信息（ip，端口，应用名）
 * 该缓存主要供注册中心使用，通过服务端注册信息到注册中心后，注册中心进行缓存
 * 之后客户端访问对应服务时，可以直接从缓存中返回对应信息给客户端
 * 当然该缓存需要有实时更新的功能，对于服务端节点的变化必须能够及时感知到
 *
 * @author: WuChengXing
 * @create: 2022-06-17 23:57
 **/
public class ApplicationCache {

    public static String APPLICATION_NAME;

    /**
     * 应用名
     */
    public static Map<String, ApplicationEntity> APPLICATION_MAP = new HashMap<>(4);

    public static Boolean add(String hostPort, ApplicationEntity entity, boolean isOverride) {
        ApplicationEntity applicationEntity = APPLICATION_MAP.get(hostPort);
        if (Objects.isNull(applicationEntity)) {
            APPLICATION_MAP.put(hostPort, entity);
            return true;
        }
        if (isOverride) {
            APPLICATION_MAP.put(hostPort, entity);
        } else {
            APPLICATION_MAP.putIfAbsent(hostPort, entity);
        }
        return true;
    }

    public static ApplicationEntity get(String hostPort) {
        ApplicationEntity applicationEntity = APPLICATION_MAP.get(hostPort);
        if (Objects.isNull(applicationEntity)) {
            return new ApplicationEntity();
        } else {
            return applicationEntity;
        }
    }
}
