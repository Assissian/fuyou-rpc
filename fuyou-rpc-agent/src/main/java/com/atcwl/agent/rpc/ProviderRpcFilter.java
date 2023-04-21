package com.atcwl.agent.rpc;

import com.atcwl.agent.trace.Span;
import com.atcwl.agent.trace.SpanContext;
import com.atcwl.agent.trace.TrackContext;
import com.atcwl.common.interfaces.impl.FuyouRpcContext;
import com.atcwl.common.interfaces.impl.InvokeFilterInfo;
import com.atcwl.core.filter.RemoteInvokeBeforeFilter;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-11 23:25
 **/
public class ProviderRpcFilter implements RemoteInvokeBeforeFilter {

    @Override
    public void filter(InvokeFilterInfo invokeFilterInfo) {

    }

    /**
     * 当接受到来自前一个客户端的调用请求后，由于Request会携带FuyouRpcContext上下文信息
     * 因此该服务在具体调用之前能够收到前一个服务的链路信息，然后利用前文信息生成当前服务的区段信息（这里是进入状态）
     * 并将该区段信息设置到SpanContext中
     * @param request
     * @return
     */
    @Override
    public FuyouRpcContext invokeBefore(FuyouRpcContext request) {
        TrackContext.setTraceId(request.getTraceId());
        Span rpcSpan = new Span(request.getTraceId(), request.getSpanId(), request.getLevel(), request.getEnterOrExit());
        //这个区段会不断的被RemoteInvokeBeforeFilter更新，但是都是enter状态的，traceId也相同，所以都是一样的
        SpanContext.setSpan(rpcSpan);
        return request;
    }
}
