package com.atcwl.core.filter;

import com.atcwl.common.interfaces.FuyouRpcFilter;
import com.atcwl.core.net.message.Response;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-05 02:02
 **/
public interface InvokeAfterFilter extends FuyouRpcFilter {

    /**
     * 调用后
     *
     * @param response
     * @return
     */
    Response invokeAfter(Response response);
}
