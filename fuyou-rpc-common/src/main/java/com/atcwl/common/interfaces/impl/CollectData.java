package com.atcwl.common.interfaces.impl;

import lombok.Data;

import java.util.Date;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 收集的数据实体
 *
 * @author: WuChengXing
 * @create: 2022-06-14 20:48
 **/
@Data
public class CollectData {

    private String traceId;

    private String spanId;

    private String parentSpanId;

    private Integer level;

    private Date entryTime;

    private Date exitTime;

    private String host;

    private String appName;

    private String clazzName;

    private String methodName;

    private String requestInfo;

    private String resultInfo;

    private String exceptionInfo;

    private Integer invokeStatus;

    private Integer invoker;

}
