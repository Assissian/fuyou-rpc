package com.atcwl.core.reflect.invoke.impl;

import com.atcwl.common.interfaces.impl.FuyouRpcContext;
import com.atcwl.core.filter.impl.FilterInvoke;
import com.atcwl.core.filter.impl.SpiLoadFilter;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.reflect.invoke.FaultTolerantInvoker;

import java.util.Date;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 抽象容错调用，主要提供了基础的invoke调用流程
 *  具体调用过程由其子类实现（这里提供了两种不同的容错机制）
 * @author: WuChengXing
 * @create: 2022-06-05 02:25
 **/
public abstract class AbstractFaultTolerantInvoker implements FaultTolerantInvoker {

    @Override
    public Response invoke(Request request) {
        SpiLoadFilter.loadFilters();
        FuyouRpcContext context = new FuyouRpcContext();
        context.setEntryTime(new Date());
        request.setFuyouRpcContext(FilterInvoke.loadInvokeBeforeFilters(context));
        Response response = doInvoke(request);
        return FilterInvoke.loadInvokeAfterFilters(response);
    }

    /**
     * 子类实现公共调用
     *
     * @param request
     * @return
     */
    protected abstract Response doInvoke(Request request);
}
