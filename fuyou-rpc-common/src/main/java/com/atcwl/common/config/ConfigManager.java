package com.atcwl.common.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.atcwl.common.annotation.FuyouRpcConfig;
import com.atcwl.common.interfaces.ConfigLoader;
import com.atcwl.common.spi.ExtensionLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 配置管理器
 *
 * @author: WuChengXing
 * @create: 2022-04-30 10:58
 **/
public class ConfigManager {

    /**
     * 按照优先级排序的加载器
     */
    private final List<ConfigLoader> configLoaders;

    private final Map<Class<?>, Object> configCache = new ConcurrentHashMap<>();

    private ConfigManager() {
        // 按照优先级放好
        List<String> configLoaderNames = Arrays.asList("system-property", "simple-rpc-property", "spi-property");
        configLoaders = new ArrayList<>(configLoaderNames.size());
        for (String loaderName : configLoaderNames) {
            ConfigLoader configLoader = ExtensionLoader.getLoader(ConfigLoader.class).getExtension(loaderName);
            configLoaders.add(configLoader);
        }
    }

    private static final ConfigManager INSTANT = new ConfigManager();

    public static ConfigManager getInstant() {
        return INSTANT;
    }


    /**
     * 加载配置，有缓存
     *
     * @param clazz 配置类型
     * @param <T>   类型
     * @return 配置实体类
     */
    @SuppressWarnings("unchecked")
    public <T> T loadConfig(Class<T> clazz) {
        T config = (T) configCache.get(clazz);
        if (config == null) {
            config = loadAndCreateConfig(clazz);
            configCache.put(clazz, config);
        }
        return config;
    }

    /**
     * 获取配置项
     *
     * @param key 配置项的 key
     * @return 如果获取不到，返回 null
     */
    public String loadConfigItem(String key) {
        // 按照优先级，先获取到就返回
        for (ConfigLoader configLoader : configLoaders) {
            String value = configLoader.loadConfigItem(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 加载并创建配置类
     *
     * @param clazz 类型 class
     * @param <T>   类型
     * @return 配置类，load 不到的字段为 null
     */
    private <T> T loadAndCreateConfig(Class<T> clazz) {
        FuyouRpcConfig configAnnotation = clazz.getAnnotation(FuyouRpcConfig.class);
        if (configAnnotation == null) {
            throw new IllegalStateException("config class " + clazz.getName() + " must has @FuyouRpcConfig annotation");
        }
        String prefix = configAnnotation.prefix();
        if (StrUtil.isBlank(prefix)) {
            throw new IllegalArgumentException("config class " + clazz.getName() + "@FuyouRpcConfig annotation must has prefix");
        }
        try {
            T configObject = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                // 忽略掉静态的
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String configKey = prefix + "." + field.getName();
                String value = loadConfigItem(configKey);
                if (value == null) {
                    continue;
                }

                Object convertedValue = Convert.convert(field.getType(), value);
                field.setAccessible(true);
                field.set(configObject, convertedValue);
            }
            return configObject;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 获取注册中心的配置
     */
    public RegistryConfig getRegistryConfig() {
        return loadConfig(RegistryConfig.class);
    }

    public BaseConfig getBaseConfig() {
        return loadConfig(BaseConfig.class);
    }

    public SimpleRpcUrl getSimpleRpcUrl() {
        return loadConfig(SimpleRpcUrl.class);
    }

}
