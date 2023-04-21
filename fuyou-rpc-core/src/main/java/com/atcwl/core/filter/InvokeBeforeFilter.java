package com.atcwl.core.filter;

import com.atcwl.common.interfaces.FuyouRpcFilter;
import com.atcwl.common.interfaces.impl.FuyouRpcContext;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 方法调用之前的过滤器
 *
 * @author: WuChengXing
 * @create: 2022-06-05 01:49
 **/
public interface InvokeBeforeFilter extends FuyouRpcFilter {

    /**
     * 调用前
     *
     * @param context
     * @return
     */
    FuyouRpcContext invokeBefore(FuyouRpcContext context);
}
