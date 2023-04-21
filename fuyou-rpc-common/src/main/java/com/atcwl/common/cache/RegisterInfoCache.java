package com.atcwl.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.common.config.RegistryConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册中心数据缓存
 *
 * @author: WuChengXing
 * @create: 2022-05-04 17:42
 **/
public class RegisterInfoCache {

    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();

    public static <T extends RegistryConfig> T getRegisterInfo(String key) {
        return (T) SERVICE_MAP.get(key);
    }

    public static <T extends RegistryConfig> void save(String key, T config) {
        SERVICE_MAP.put(key, config);
    }

    public static Boolean remove(List<String> urls) {
        if (CollectionUtil.isEmpty(urls)) {
            return false;
        }
        for (String url : urls) {
            SERVICE_MAP.remove(url);
        }
        return true;
    }
}
