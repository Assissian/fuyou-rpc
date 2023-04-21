package com.atcwl.agent.trace;


import com.atcwl.agent.constant.AgentConstant;

import java.util.Objects;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述: trace的全局上下文信息
 * @author: WuChengXing
 * @create: 2022-06-07 01:32
 **/
public class SpanContext {

    private static final ThreadLocal<Span> trackLocal = new ThreadLocal<>();

    public static void clear() {
        trackLocal.remove();
    }

    public static Span getSpan() {
        return trackLocal.get();
    }

    public static void setSpan(Span span) {
        trackLocal.set(span);
    }

    /**
     * 方法进入的算法：
     * - 从当前的span上下文中拿到对应的信息
     * - 计算新的spanId = 当前的spanId + “.” + (level + 1)
     * - 如果是从退出状态过度过来的，那么将level重置为0，继续深入方法
     * - 将状态改成进入状态
     *
     * @param spanContext
     * @return
     */
    public static Span calEntrySpan(Span spanContext) {
        String traceId = spanContext.getTraceId();
        String newSpanId = spanContext.getSpanId();
        Integer newEnterOrExit = spanContext.getEnterOrExit();
        Integer newLevel = spanContext.getLevel();
        if (Objects.isNull(newLevel)) {
            newLevel = 0;
        }
        if (Objects.isNull(newEnterOrExit)) {
            newEnterOrExit = 1;
        }
        newSpanId = newSpanId + "." + (newLevel + 1);
        if (AgentConstant.EXIT.equals(newEnterOrExit)) {
            newLevel = 0;
            newEnterOrExit = AgentConstant.ENTRY;
        }
        return new Span(traceId, newSpanId, newLevel, newEnterOrExit);
    }

    /**
     * 方法退出算法：
     * -
     * @param spanContext
     * @return
     */
    public static Span calExitSpan(Span spanContext) {
        String traceId = spanContext.getTraceId();
        String spanId = spanContext.getSpanId();
        String[] split = spanId.split("\\.");
        if (split.length < 1) {
            return new Span(traceId, "0", 0, 2);
        }
        int i = spanId.lastIndexOf(".");
        Integer newLevel = Integer.valueOf(spanId.substring(i + 1));
        String newSpanId = i < 0 ? "" : spanId.substring(0, i);
        return new Span(traceId, newSpanId, newLevel, 2);
    }
}
