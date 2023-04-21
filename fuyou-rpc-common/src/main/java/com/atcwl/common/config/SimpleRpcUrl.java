package com.atcwl.common.config;


import lombok.Data;

import java.util.Objects;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: url信息，主要是记录你使用的注册中心的类型（Redis，Mysql还是其他）
 * 以及和注册中心使用相关的信息，主要在与注册中心通信时使用
 *
 * @author: WuChengXing
 * @create: 2022-04-30 10:55
 **/
@Data
public class SimpleRpcUrl {
    /**
     * 注册中心类型，如Redis，Mysql等
     */
    private String type;
    /**
     *注册中心地址
     */
    private String host;
    /**
     *注册中心的端口
     */
    private Integer port;
    /**
     *登录注册中心的用户名
     */
    private String username;
    /**
     *登录注册中心的密码
     */
    private String password;
    /**
     *注册中心使用的数据库名
     * 这两个属性主要针对Mysql的情况
     */
    private String database;
    /**
     *表名
     */
    private String table;

    /**
     * 将对应注册配置信息转化为对应URL信息，方便后续获取对应注册中心对象
     * @param config
     * @param <T>
     * @return
     */
    public static <T extends RegistryConfig> SimpleRpcUrl toSimpleRpcUrl(T config) {
        if (Objects.isNull(config)) {
            throw new RuntimeException("为获取到注册中心配置");
        }
        // 构建url其他参数
        SimpleRpcUrl simpleRpcUrl = buildSimpleRpcUrl(config);
        parseUrl(simpleRpcUrl, config);
        return simpleRpcUrl;
    }

    /**
     * 解析注册中心配置
     * @param simpleRpcUrl
     * @param config
     */
    private static void parseUrl(SimpleRpcUrl simpleRpcUrl, RegistryConfig config) {
        String address = config.getAddress();
        String[] split = address.split("://");
        if (split.length < 1) {
            throw new RuntimeException("不支持该注册中心地址格式");
        }
        simpleRpcUrl.setType(split[0]);
        String[] infos = split[1].split(":");
        if (infos.length < 1) {
            throw new RuntimeException("不支持该注册中心地址格式");
        }
        simpleRpcUrl.setHost(infos[0]);
        simpleRpcUrl.setPort(Integer.valueOf(infos[1]));
    }

    private static SimpleRpcUrl buildSimpleRpcUrl(RegistryConfig config) {
        SimpleRpcUrl simpleRpcUrl = new SimpleRpcUrl();
        simpleRpcUrl.setUsername(config.getUsername());
        simpleRpcUrl.setPassword(config.getPassword());
        simpleRpcUrl.setDatabase(config.getDatabase());
        simpleRpcUrl.setTable(config.getTable());
        return simpleRpcUrl;
    }
}
