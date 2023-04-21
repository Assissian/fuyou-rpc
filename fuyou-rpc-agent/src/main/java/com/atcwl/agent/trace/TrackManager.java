package com.atcwl.agent.trace;

import cn.hutool.core.util.StrUtil;

import java.util.Stack;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述:
 * 路径管理器
 * @author: WuChengXing
 * @create: 2022-06-03 00:47
 **/
public class TrackManager {
    /**
     * 存储到目前调用过程中经过的服务链路的信息
     * 调用服务时，Span区段信息进栈，服务调用完成返回时Span出栈
     */
    private static final ThreadLocal<Stack<Span>> track = new ThreadLocal<>();

    private static Span createSpan() {
        Stack<Span> stack = track.get();
        if (stack == null) {
            stack = new Stack<>();
            track.set(stack);
        }
        String traceId;
        if (stack.isEmpty()) {
            traceId = TrackContext.getTraceId();
            if (traceId == null) {
                traceId = "nvl";
                TrackContext.setTraceId(traceId);
            }
        } else {
            Span span = stack.peek();
            traceId = span.getTraceId();
            TrackContext.setTraceId(traceId);
        }
        return new Span(traceId);
    }

    /**
     * 服务调用，此时需要创建对应Span信息并进栈
     * @return
     */
    public static Span createEntrySpan() {
        Span span = createSpan();
        Stack<Span> stack = track.get();
        stack.push(span);
        return span;
    }

    /**
     * 服务调用返回，出栈Span信息
     * @return
     */
    public static Span getExitSpan() {
        Stack<Span> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            TrackContext.clear();
            return null;
        }
        return stack.pop();
    }

    /**
     * 获取当前的服务链路信息
     * @return
     */
    public static Span getCurrentSpan() {
        //如果当前已经生成了新的路径ID，那么直接以该路径ID构建一个Span即可
        if (!StrUtil.isBlank(TrackContext.getTraceId())) {
            return new Span(TrackContext.getTraceId());
        }
        //获取不到traceId时说明没有脱离当前区段，因此返回之前的Span即可
        Stack<Span> stack = track.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }
}
