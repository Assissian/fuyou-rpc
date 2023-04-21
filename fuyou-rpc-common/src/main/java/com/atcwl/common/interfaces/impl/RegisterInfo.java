package com.atcwl.common.interfaces.impl;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册信息
 * 注册信息的内容和Request非常接近，不过RegisterInfo只在注册服务时使用
 * 对于服务注册而言使用Request不太合适
 * @author: WuChengXing
 * @create: 2022-05-13 16:54
 **/
@Data
public class RegisterInfo {

    /**
     * 接口
     */
    private String interfaceName;

    /**
     * 实现类对应的beanName
     */
    private String beanName;

    /**
     * 别名
     */
    private String alias;

    /**
     * 设置超时时间
     */
    private Long timeout;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * nettyServer端的host
     */
    private String host;

    /**
     * nettyServer端的port
     */
    private Integer port;

    /**
     * 负载均衡算法
     */
    private String loadBalanceRule;

    /**
     * 权重算法的时候的权重值
     */
    private Integer weights;

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
     * 应用名称
     */
    private String applicationName;

    /**
     * 服务健康状态；1=健康
     */
    private String health;
}
