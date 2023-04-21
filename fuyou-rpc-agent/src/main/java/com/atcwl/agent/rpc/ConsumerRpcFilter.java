package com.atcwl.agent.rpc;

import com.atcwl.agent.trace.Span;
import com.atcwl.agent.trace.SpanContext;
import com.atcwl.agent.trace.TrackManager;
import com.atcwl.common.interfaces.impl.FuyouRpcContext;
import com.atcwl.common.interfaces.impl.InvokeFilterInfo;
import com.atcwl.core.filter.InvokeBeforeFilter;

import java.util.Objects;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 客户端执行RPC调用后，在服务端方法执行之前执行前置过滤器
 *
 * @author: WuChengXing
 * @create: 2022-06-11 23:25
 **/
public class ConsumerRpcFilter implements InvokeBeforeFilter {
    
    @Override
    public void filter(InvokeFilterInfo invokeFilterInfo) {
        
    }

    /**
     * 前置过滤方法，和拦截器的前置拦截方法类似
     * @param context
     * @return
     */
    @Override
    public FuyouRpcContext invokeBefore(FuyouRpcContext context) {
        //前一个区段对象，用于获取traceId
        Span span = TrackManager.getCurrentSpan();
        context.setTraceId(Objects.isNull(span) ? null : span.getTraceId());
        //获取服务的区段对象，其中有过滤器执行所需的上下文信息
        Span contextSpan = SpanContext.getSpan();
        context.setSpanId(Objects.isNull(contextSpan) ? null : contextSpan.getSpanId());
        context.setLevel(Objects.isNull(contextSpan) ? null : contextSpan.getLevel());
        context.setEnterOrExit(Objects.isNull(contextSpan) ? null : contextSpan.getEnterOrExit());
        return context;
    }
}
