package com.atcwl.common.interfaces;


import com.atcwl.common.annotation.FuyouRpcSPI;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.interfaces.impl.RegisterInfo;
import com.atcwl.common.network.HookEntity;

import java.util.Map;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-04-21 17:00
 **/
@FuyouRpcSPI(value = "redis")
public interface RegisterCenter {

    /**
     * 初始化注册中心
     *
     * @param url
     */
    void init(SimpleRpcUrl url);

    /**
     * 服务注册
     *
     * @param request
     * @return
     */
    String register(RegisterInfo request);

    /**
     * 获取服务
     *
     * @param request
     * @return
     */
    String get(RegisterInfo request);

    /**
     * 注销服务
     *
     * @param hookEntity
     * @return
     */
    Boolean unregister(HookEntity hookEntity);

    /**
     * 优雅上下线的时候，置为下线状态 health = 0
     *
     * @return
     */
    Boolean offline();

    /**
     * 应用上线，置为上线状态 health = 1
     *
     * @return
     */
    Boolean online();

    /**
     * 检查服务状态
     *
     * @return
     */
    Boolean checkHealth();

    /**
     * 过滤不健康的服务，不做负载，数据格式是：url + request 的数据格式
     *
     * @param registerInfos
     */
    void filterNotHealth(Map<String, String> registerInfos);

}
