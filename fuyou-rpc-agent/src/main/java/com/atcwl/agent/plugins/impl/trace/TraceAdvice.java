package com.atcwl.agent.plugins.impl.trace;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.atcwl.agent.data.DataBuilder;
import com.atcwl.agent.trace.Span;
import com.atcwl.agent.trace.SpanContext;
import com.atcwl.agent.trace.TrackContext;
import com.atcwl.agent.trace.TrackManager;
import com.atcwl.agent.util.AgentLog;
import com.atcwl.common.cache.ApplicationCache;
import net.bytebuddy.asm.Advice;

import java.util.Objects;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-03 00:48
 **/
public class TraceAdvice {

    @Advice.OnMethodEnter()
    public static void enter(@Advice.Origin("#t") String className,
                             @Advice.Origin("#m") String methodName,
                             @Advice.AllArguments Object[] args) {
        AgentLog.info("进入方法，类方法信息：{}, 参数：{}", className + "#" + methodName, JSON.toJSONString(args));
        Span currentSpan = TrackManager.getCurrentSpan();
        String traceId = "";
        int invoker = 0;
        if (null == currentSpan) {
            Snowflake snowflake = IdUtil.getSnowflake(1, 1);
            traceId = Long.toString(snowflake.nextId());
            invoker = 1;
            TrackContext.setTraceId(traceId);
        }
        Span spanContext = SpanContext.getSpan();
        String preSpanId = "";
        if (spanContext == null) {
            spanContext = new Span(traceId, "0", 0, 1);
        } else {
            preSpanId = spanContext.getSpanId();
            spanContext = SpanContext.calEntrySpan(spanContext);
        }
        SpanContext.setSpan(spanContext);
        TrackManager.createEntrySpan();
        DataBuilder.buildEntry(ApplicationCache.APPLICATION_NAME, className, methodName, spanContext.getTraceId(),
                spanContext.getSpanId(), preSpanId, spanContext.getEnterTime(), spanContext.getLevel(),
                JSON.toJSONString(args), invoker);
        AgentLog.info("进入方法，当前Span信息：{}", JSON.toJSONString(spanContext));
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Origin("#t") String className,
                            @Advice.Origin("#m") String methodName,
                            @Advice.Return String returnInfo,
                            @Advice.Thrown Throwable thrown) {
        AgentLog.info("退出方法，类方法信息：{}， 返回值信息：{}", className + "#" + methodName, returnInfo);
        String exceptionInfo = "";
        if (!Objects.isNull(thrown)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(thrown.getClass().getName()).append("\n");
            stringBuilder.append(thrown.getMessage()).append("\n");
            exceptionInfo = stringBuilder.toString();
            AgentLog.error("异常类型：{}, 异常信息：{}", thrown.getClass().getName(), thrown.getMessage());
        }
        Span spanContext = SpanContext.getSpan();
        String preSpanId = spanContext.getSpanId();
        spanContext = SpanContext.calExitSpan(spanContext);
        SpanContext.setSpan(spanContext);
        Span exitSpan = TrackManager.getExitSpan();
        DataBuilder.buildExist(ApplicationCache.APPLICATION_NAME, className, methodName, spanContext.getTraceId(),
                spanContext.getSpanId(), preSpanId, spanContext.getEnterTime(), spanContext.getLevel(),
                JSON.toJSONString(returnInfo), exceptionInfo);
        if (null == exitSpan) {
            return;
        }
        AgentLog.info("推出方法，当前Span信息：{}", JSON.toJSONString(spanContext));
    }
}
