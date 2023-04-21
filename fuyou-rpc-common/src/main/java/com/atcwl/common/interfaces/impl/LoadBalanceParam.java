package com.atcwl.common.interfaces.impl;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 负载相关的配置类
 *  主要就是记录了负载的URL权重
 * @author: WuChengXing
 * @create: 2022-05-11 20:37
 **/
@Data
public class LoadBalanceParam {
    /**
     * 权重数值
     */
    private Integer weights;
}
