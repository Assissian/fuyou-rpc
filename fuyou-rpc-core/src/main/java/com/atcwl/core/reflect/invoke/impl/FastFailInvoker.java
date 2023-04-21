package com.atcwl.core.reflect.invoke.impl;

import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.reflect.invoke.NettyInvoker;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 容错：快速失败
 *
 * @author: WuChengXing
 * @create: 2022-06-04 19:11
 **/
public class FastFailInvoker extends AbstractFaultTolerantInvoker {

    /**
     * 快速失败机制只会调用一次，若获取不到结果就会直接失败
     * @param request
     * @return
     */
    @Override
    protected Response doInvoke(Request request) {
        //这里也是一种容错，只是快速失败下只需要一次调用就可以了
        return NettyInvoker.send(request);
    }
}
