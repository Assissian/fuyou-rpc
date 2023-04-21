package com.atcwl.core.filter.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.common.constrant.FilterConstant;
import com.atcwl.common.interfaces.FuyouRpcFilter;
import com.atcwl.common.spi.ExtensionLoader;
import com.atcwl.core.filter.InvokeAfterFilter;
import com.atcwl.core.filter.InvokeBeforeFilter;
import com.atcwl.core.filter.RemoteInvokeBeforeFilter;
import com.atcwl.core.net.cache.FilterCache;

import java.util.Map;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: spi机制加载filter
 *
 * @author: WuChengXing
 * @create: 2022-06-05 11:47
 **/
public class SpiLoadFilter {

    public static void loadFilters() {
        ExtensionLoader<FuyouRpcFilter> loader = ExtensionLoader.getLoader(FuyouRpcFilter.class);
        Map<String, Class<?>> extensionClassesCache = loader.getExtensionClassesCache();
        if (!CollectionUtil.isEmpty(extensionClassesCache)) {
            extensionClassesCache.forEach((k, v) -> {
                try {
                    if (InvokeBeforeFilter.class.isAssignableFrom(v)) {
                        FilterCache.addInvokeFilter(FilterConstant.INVOKE_BEFORE_FILTER, v.getConstructor(null).newInstance());
                    } else if (InvokeAfterFilter.class.isAssignableFrom(v)) {
                        FilterCache.addInvokeFilter(FilterConstant.INVOKE_AFTER_FILTER, v.getConstructor(null).newInstance());
                    } else if (RemoteInvokeBeforeFilter.class.isAssignableFrom(v)) {
                        FilterCache.addInvokeFilter(FilterConstant.REMOTE_INVOKE_BEFORE_FILTER, v.getConstructor(null).newInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
