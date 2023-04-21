package com.atcwl.core.register;

import java.util.Map;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 健康检测，用于检测服务的健康状态，该接口和心跳包结合使用
 *
 * @author: WuChengXing
 * @create: 2022-09-01 10:13
 **/
public interface HealthDetect {

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
