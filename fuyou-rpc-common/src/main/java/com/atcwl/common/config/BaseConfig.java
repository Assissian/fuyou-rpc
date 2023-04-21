package com.atcwl.common.config;

import com.atcwl.common.annotation.FuyouRpcConfig;
import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: rpc的一些基础配置，全局配置等
 *  这些基础配置一般都有默认值，所以并不想ConsumerConfig那样，一定需要进行配置，所以分开设计
 * @author: WuChengXing
 * @create: 2022-05-06 16:34
 **/
@Data
@FuyouRpcConfig(prefix = "simple.rpc.base")
public class BaseConfig {

    /**
     * 负载均衡算法
     */
    private String loadBalanceRule;

    /**
     * 权重算法的时候的权重值
     */
    private Integer weights;

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * 序列化的SPI类型
     */
    private String serializer;

    /**
     * 压缩的SPI类型
     */
    private String compressor;

    /**
     * 注册中心的SPI类型
     */
    private String register;

    /**
     * 心跳间隔时间
     */
    private Long beatIntervalTime;

    /**
     * 客户端停止心跳或者写操作后，服务端断开时间
     */
    private Long stopConnectTime;

    /**
     * 服务端的port配置
     */
    private Integer port;

    /**
     * 容错类型
     */
    private String faultTolerantType;
}
