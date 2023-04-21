package com.atcwl.core.reflect.invoke;

import com.atcwl.common.annotation.FuyouRpcSPI;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 容错invoker
 *  该接口实现了容错调用的规范
 * @author: WuChengXing
 * @create: 2022-06-04 18:44
 **/
@FuyouRpcSPI("fast-fail")
public interface FaultTolerantInvoker {

    /**
     * 远程调用
     *
     * @param request
     * @return
     */
    Response invoke(Request request);
}
