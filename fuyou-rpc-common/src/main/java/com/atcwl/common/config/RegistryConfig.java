package com.atcwl.common.config;

import com.atcwl.common.annotation.FuyouRpcConfig;
import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册中心的配置
 *  建立连接时会构建注册中心的信息，那时需要依赖于该配置，并且注册中心一般由用户设计选择
 *  所以为了通用性，需要提供相应的配置手段，其实就类似于SpringBoot的个性化配置
 * @author: WuChengXing
 * @create: 2022-04-30 10:50
 **/
@Data
@FuyouRpcConfig(prefix = "simple.rpc.register")
public class RegistryConfig {

    /**
     * 这里的定义规定：redis://127.0.0.1:6379 mysql://127.0.0.1:3306 zk://127.0.0.1:2181等
     */
    private String address;

    private String username;

    private String password;

    private String database;

    private String table;
}
