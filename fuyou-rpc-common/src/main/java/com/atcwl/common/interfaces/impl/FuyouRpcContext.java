package com.atcwl.common.interfaces.impl;

import lombok.Data;

import java.util.Date;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 上下文信息
 * 主要和链路追踪有关
 * @author: WuChengXing
 * @create: 2022-06-05 14:31
 **/
@Data
public class FuyouRpcContext {

    private String traceId;

    private Date entryTime;

    private String spanId;

    private Integer level;

    /**
     * 1=进入，2=退出
     */
    private Integer enterOrExit;
}
