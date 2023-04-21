package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;
import com.atcwl.common.interfaces.impl.InvokeFilterInfo;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 过滤器
 *
 * @author: WuChengXing
 * @create: 2022-06-04 23:49
 **/
@FuyouRpcSPI
public interface FuyouRpcFilter {

    /**
     * 拦截器
     *
     * @param invokeFilterInfo
     */
    void filter(InvokeFilterInfo invokeFilterInfo);
}
