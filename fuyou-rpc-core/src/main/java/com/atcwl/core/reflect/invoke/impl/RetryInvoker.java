package com.atcwl.core.reflect.invoke.impl;

import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.reflect.invoke.NettyInvoker;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 容错：重试机制
 *
 * @author: WuChengXing
 * @create: 2022-06-04 20:12
 **/
public class RetryInvoker extends AbstractFaultTolerantInvoker {
    /**
     * 允许在规定重试次数内重新尝试调用，若重试期间内能够获得返回结果，则正常返回Response
     * 若超过重试次数上限都未获得结果，则报错
     * @param request
     * @return
     */
    @Override
    protected Response doInvoke(Request request) {
        Response response = null;
        //这里是容错机制
        for (int i = 0; i < request.getRetryNum(); i++) {
            //调用NettyInvoker来发送请求
            response = NettyInvoker.send(request);
        }
        return response;
    }
}
