package com.atcwl.core.reflect.invoke;


import com.atcwl.common.spi.ExtensionLoader;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 容错机制，该类的作用为适配器，帮助进行服务调用，同时保存返回的响应，等到客户端来拿
 *
 * @author: WuChengXing
 * @create: 2022-08-28 17:03
 **/
public class MultiInvoker implements Runnable {

    private final Request request;
    private final String faultTolerantType;
    private Response response;

    public MultiInvoker(Request request, String faultTolerantType) {
        this.request = request;
        this.faultTolerantType = faultTolerantType;
    }

    @Override
    public void run() {
        // 容错机制
        FaultTolerantInvoker faultTolerantInvoker = ExtensionLoader.getLoader(FaultTolerantInvoker.class)
                .getExtension(faultTolerantType);
        //执行容错机制下的调用
        this.response = faultTolerantInvoker.invoke(request);
    }

    public Response getResponse() {
        return response;
    }
}
