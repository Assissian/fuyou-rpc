package com.atcwl.common.interfaces.impl;

import lombok.Data;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 过滤器实体
 *  与Request类似，和RegisterInfo的作用是相近的
 * @author: WuChengXing
 * @create: 2022-06-05 00:32
 **/
@Data
public class InvokeFilterInfo {
    /**
     * 此次请求的id
     */
    private long requestId;

    /**
     * 方法
     */
    private String methodName;

    /**
     * 属性
     */
    private List<String> paramTypes;

    /**
     * 入参
     */
    private Object[] args;

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
     * 心跳间隔时间
     */
    private Long beatIntervalTime;

    /**
     * 客户端停止心跳或者写操作后，服务端断开时间
     */
    private Long stopConnectTime;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 服务健康状态；1=健康
     */
    private String health;

    public static InvokeFilterInfo register2Request(RegisterInfo info) {
        InvokeFilterInfo invokeFilterInfo = new InvokeFilterInfo();
        invokeFilterInfo.setHost(info.getHost());
        invokeFilterInfo.setPort(info.getPort());
        invokeFilterInfo.setInterfaceName(info.getInterfaceName());
        invokeFilterInfo.setTimeout(info.getTimeout());
        invokeFilterInfo.setRetryNum(info.getRetryNum());
        invokeFilterInfo.setBeanName(info.getBeanName());
        invokeFilterInfo.setAlias(info.getAlias());
        invokeFilterInfo.setLoadBalanceRule(info.getLoadBalanceRule());
        invokeFilterInfo.setSerializer(info.getSerializer());
        invokeFilterInfo.setCompressor(info.getCompressor());
        invokeFilterInfo.setRegister(info.getRegister());
        invokeFilterInfo.setWeights(info.getWeights());
        invokeFilterInfo.setApplicationName(info.getApplicationName());
        invokeFilterInfo.setHealth(info.getHealth());
        return invokeFilterInfo;
    }

    public static RegisterInfo request2Register(InvokeFilterInfo request) {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setInterfaceName(request.getInterfaceName());
        registerInfo.setBeanName(request.getBeanName());
        registerInfo.setAlias(request.getAlias());
        registerInfo.setTimeout(request.getTimeout());
        registerInfo.setRetryNum(request.getRetryNum());
        registerInfo.setHost(request.getHost());
        registerInfo.setPort(request.getPort());
        registerInfo.setLoadBalanceRule(request.getLoadBalanceRule());
        registerInfo.setWeights(request.getWeights());
        registerInfo.setSerializer(request.getSerializer());
        registerInfo.setCompressor(request.getCompressor());
        registerInfo.setRegister(request.getRegister());
        registerInfo.setApplicationName(request.getApplicationName());
        registerInfo.setHealth(request.getHealth());
        return registerInfo;
    }
}
