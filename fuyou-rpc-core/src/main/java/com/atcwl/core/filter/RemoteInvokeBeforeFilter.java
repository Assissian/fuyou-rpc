package com.atcwl.core.filter;

import com.atcwl.common.interfaces.FuyouRpcFilter;
import com.atcwl.common.interfaces.impl.FuyouRpcContext;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 远程方法调用之前的拦截器
 *
 * @author: WuChengXing
 * @create: 2022-06-05 02:03
 **/
public interface RemoteInvokeBeforeFilter extends FuyouRpcFilter {

    /**
     * 调用前
     *
     * @param request
     * @return
     */
    FuyouRpcContext invokeBefore(FuyouRpcContext request);
}
