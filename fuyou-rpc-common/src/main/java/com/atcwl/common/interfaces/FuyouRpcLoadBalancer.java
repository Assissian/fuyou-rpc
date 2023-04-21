package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;

import java.util.Map;

/**
 * 负载均衡接口，用于实现负载均衡相关规范
 * 默认轮询策略
 * @Author cwl
 * @date
 * @apiNote
 */
@FuyouRpcSPI("round")
public interface FuyouRpcLoadBalancer {
    /**
     * 负载均衡，根据用户选择的负载均衡策略进行
     * @param services
     * @return
     */
    String loadBalance(Map<String, String> services);
}
