package com.atcwl.core.filter.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.common.interfaces.impl.FuyouRpcContext;
import com.atcwl.core.filter.InvokeAfterFilter;
import com.atcwl.core.filter.InvokeBeforeFilter;
import com.atcwl.core.filter.RemoteInvokeBeforeFilter;
import com.atcwl.core.net.cache.FilterCache;
import com.atcwl.core.net.message.Response;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 过滤器调用
 *
 * @author: WuChengXing
 * @create: 2022-06-05 13:56
 **/
public class FilterInvoke {

    public static FuyouRpcContext loadInvokeBeforeFilters(FuyouRpcContext simpleRpcContext) {
        FuyouRpcContext updateContext = simpleRpcContext;
        List<InvokeBeforeFilter> invokeBeforeFilters = FilterCache.allInvokeBeforeFilters();
        if (!CollectionUtil.isEmpty(invokeBeforeFilters)) {
            for (InvokeBeforeFilter invokeBeforeFilter : invokeBeforeFilters) {
                updateContext = invokeBeforeFilter.invokeBefore(simpleRpcContext);
            }
        }
        return updateContext;
    }

    public static Response loadInvokeAfterFilters(Response response) {
        Response updateResponse = response;
        List<InvokeAfterFilter> invokeAfterFilters = FilterCache.allInvokeAfterFilter();
        if (!CollectionUtil.isEmpty(invokeAfterFilters)) {
            for (InvokeAfterFilter invokeAfterFilter : invokeAfterFilters) {
                updateResponse = invokeAfterFilter.invokeAfter(response);
            }
        }
        return updateResponse;
    }

    public static FuyouRpcContext loadRemoteInvokeBeforeFilters(FuyouRpcContext context) {
        FuyouRpcContext updateContext = context;
        List<RemoteInvokeBeforeFilter> remoteInvokeBeforeFilters = FilterCache.allRemoteInvokeBeforeFilter();
        if (!CollectionUtil.isEmpty(remoteInvokeBeforeFilters)) {
            for (RemoteInvokeBeforeFilter remoteInvokeBeforeFilter : remoteInvokeBeforeFilters) {
                updateContext = remoteInvokeBeforeFilter.invokeBefore(context);
            }
        }
        return updateContext;
    }
}
