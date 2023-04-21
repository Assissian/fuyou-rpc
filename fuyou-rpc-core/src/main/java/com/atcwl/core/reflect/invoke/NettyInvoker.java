package com.atcwl.core.reflect.invoke;

import com.atcwl.common.exception.network.NettyInvokeException;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.net.send.SyncWrite;

import java.util.Objects;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: netty的invoker，用于实际发送请求
 * 由于发送请求的动作都是相同的，所以单独抽象了一个NettyInvoker出来，将请求发送流程与容错流程分离
 *  容错的Invoker可以有多种，所以将发送请求的行为独立出来更好，可以减少代码冗余，方便扩展更多的容错机制
 * @author: WuChengXing
 * @create: 2022-06-04 17:02
 **/
public class NettyInvoker {

    public static Response send(Request request) {
        // 发送请求
        Response response = null;
        try {
            response = new SyncWrite().writeAndSync(request.getChannel(), request,
                    Objects.isNull(request.getTimeout()) ? 30L : request.getTimeout());
        } catch (Exception e) {
            throw new NettyInvokeException(e.getMessage());
        }

        //异步调用
        return response;
    }
}
