package com.atcwl.core.net.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.common.constrant.FilterConstant;
import com.atcwl.common.interfaces.FuyouRpcFilter;
import com.atcwl.core.filter.InvokeAfterFilter;
import com.atcwl.core.filter.InvokeBeforeFilter;
import com.atcwl.core.filter.RemoteInvokeBeforeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 过滤器缓存
 *
 * @author: WuChengXing
 * @create: 2022-06-05 02:07
 **/
public class FilterCache {

    public static final String INVOKE_BEFORE_FILTER = FilterConstant.INVOKE_BEFORE_FILTER;
    public static final String INVOKE_AFTER_FILTER = FilterConstant.INVOKE_AFTER_FILTER;
    public static final String REMOTE_INVOKE_BEFORE_FILTER = FilterConstant.REMOTE_INVOKE_BEFORE_FILTER;

    public static Map<String, List<InvokeBeforeFilter>> INVOKE_BEFORE_FILTERS = new ConcurrentHashMap<>(4);
    public static Map<String, List<InvokeAfterFilter>> INVOKE_AFTER_FILTERS = new ConcurrentHashMap<>(4);
    public static Map<String, List<RemoteInvokeBeforeFilter>> REMOTE_INVOKE_BEFORE_FILTERS = new ConcurrentHashMap<>(4);

    public static List<InvokeBeforeFilter> allInvokeBeforeFilters() {
        List<InvokeBeforeFilter> invokeBeforeFilters = INVOKE_BEFORE_FILTERS.get(INVOKE_BEFORE_FILTER);
        if (CollectionUtil.isEmpty(invokeBeforeFilters)) {
            invokeBeforeFilters = new ArrayList<>();
            INVOKE_BEFORE_FILTERS.put(INVOKE_BEFORE_FILTER, invokeBeforeFilters);
        }
        return INVOKE_BEFORE_FILTERS.get(INVOKE_BEFORE_FILTER);
    }

    public static List<InvokeAfterFilter> allInvokeAfterFilter() {
        List<InvokeAfterFilter> invokeAfterFilters = INVOKE_AFTER_FILTERS.get(INVOKE_AFTER_FILTER);
        if (CollectionUtil.isEmpty(invokeAfterFilters)) {
            invokeAfterFilters = new ArrayList<>();
            INVOKE_AFTER_FILTERS.put(INVOKE_AFTER_FILTER, invokeAfterFilters);
        }
        return INVOKE_AFTER_FILTERS.get(INVOKE_AFTER_FILTER);
    }

    public static List<RemoteInvokeBeforeFilter> allRemoteInvokeBeforeFilter() {
        List<RemoteInvokeBeforeFilter> remoteInvokeBeforeFilters = REMOTE_INVOKE_BEFORE_FILTERS.get(REMOTE_INVOKE_BEFORE_FILTER);
        if (CollectionUtil.isEmpty(remoteInvokeBeforeFilters)) {
            remoteInvokeBeforeFilters = new ArrayList<>();
            REMOTE_INVOKE_BEFORE_FILTERS.put(REMOTE_INVOKE_BEFORE_FILTER, remoteInvokeBeforeFilters);
        }
        return REMOTE_INVOKE_BEFORE_FILTERS.get(REMOTE_INVOKE_BEFORE_FILTER);
    }

    @SuppressWarnings("all")
    public static <T extends FuyouRpcFilter> T addInvokeFilter(String filterType, Object filter) {
        if (INVOKE_BEFORE_FILTER.equals(filterType)) {
            return (T) addInvokeBeforeFilter((InvokeBeforeFilter) filter);
        } else if (INVOKE_AFTER_FILTER.equals(filterType)) {
            return (T) addInvokeAfterFilter((InvokeAfterFilter) filter);
        } else if (REMOTE_INVOKE_BEFORE_FILTER.equals(filterType)) {
            return (T) addRemoteInvokeBeforeFilter((RemoteInvokeBeforeFilter) filter);
        }
        return null;
    }

    public static InvokeBeforeFilter addInvokeBeforeFilter(InvokeBeforeFilter invokeBeforeFilter) {
        allInvokeBeforeFilters().add(invokeBeforeFilter);
        return invokeBeforeFilter;
    }

    public static InvokeAfterFilter addInvokeAfterFilter(InvokeAfterFilter invokeAfterFilter) {
        allInvokeAfterFilter().add(invokeAfterFilter);
        return invokeAfterFilter;
    }

    public static RemoteInvokeBeforeFilter addRemoteInvokeBeforeFilter(RemoteInvokeBeforeFilter remoteInvokeBeforeFilter) {
        allRemoteInvokeBeforeFilter().add(remoteInvokeBeforeFilter);
        return remoteInvokeBeforeFilter;
    }
}
