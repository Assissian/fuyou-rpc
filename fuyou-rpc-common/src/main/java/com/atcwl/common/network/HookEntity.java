package com.atcwl.common.network;

import lombok.Data;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 退出钩子函数实体
 *  主要作用是封装参数，不然解绑时直接传参会导致方法参数臃肿
 * @author: WuChengXing
 * @create: 2022-05-10 15:09
 **/
@Data
public class HookEntity {

    /**
     * 注册的rpc服务的名称：这里的话是 com.simple.rpc.AService_aService
     * 其实就是对应服务提供的所有接口名称的集合
     */
    private List<String> rpcServiceNames;

    /**
     * 对应停止服务的server端的连接信息
     */
    private String serverUrl;

    private Integer serverPort;

    /**
     * 注册中心类型
     */
    private String registerType;

    private String applicationName;
}
