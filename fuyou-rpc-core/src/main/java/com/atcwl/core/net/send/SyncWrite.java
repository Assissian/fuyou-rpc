package com.atcwl.core.net.send;

import com.atcwl.common.constrant.MessageFormatConstant;
import com.atcwl.common.constrant.enums.CompressType;
import com.atcwl.common.constrant.enums.MessageType;
import com.atcwl.common.constrant.enums.SerializerType;
import com.atcwl.common.exception.network.NettyInitException;
import com.atcwl.common.exception.network.NettyResponseException;
import com.atcwl.core.net.message.FuyouRpcMessage;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class SyncWrite {
    public Response writeAndSync(Channel channel, Request request, Long timeout) throws Exception {
        if (channel == null) {
            throw new NettyInitException("channel is null, please init channel");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (timeout <= 0) {
            timeout = 30L;
        }
        long requestId = MessageFormatConstant.REQUEST_ID.getAndIncrement();
        request.setRequestId(requestId);
        //这里已经要发送请求了，所以我们先构造一个Future用于暂存该请求的结果用
        //我们以RequestID为key进行缓存，需要提前确定
        WriteFuture<Response> syncWriteFuture = new SyncWriteFuture(requestId);
        SyncWriteMap.CLIENT_CACHE.put(requestId, syncWriteFuture);

        FuyouRpcMessage message = new FuyouRpcMessage();
        message.setRequestId(requestId);
        message.setMessageType(MessageType.REQUEST.getType());
        byte serializerType = SerializerType.fromName(request.getSerializer()).getType();
        message.setSerializeType(serializerType);
        byte compressType = CompressType.fromName(request.getCompressor()).getType();
        message.setCompressType(compressType);

        Response response = doWriteAndSync(channel, message, syncWriteFuture, timeout);
        SyncWriteMap.CLIENT_CACHE.remove(requestId);
        return response;
    }

    private Response doWriteAndSync(Channel channel, FuyouRpcMessage message, WriteFuture<Response> syncWriteFuture, Long timeout) throws Exception {
        //调用channel方法写入请求
        //由于netty为NIO，所以该写操作并不会阻塞，而是直接向下执行
        channel.writeAndFlush(message)
                .addListener((ChannelFutureListener) future -> {
                    //Response的设置是由handler完成的，这里只是在写出请求后设置一些基础数据，比如写出是否成功
                    syncWriteFuture.setWriteResult(future.isSuccess());
                    syncWriteFuture.setCause(future.cause());
                    if (!syncWriteFuture.isWriteSuccess()) {
                        SyncWriteMap.CLIENT_CACHE.remove(message.getRequestId());
                    }
                });
        //为了能够拿到结果，这里我们需要模拟一个等待操作，模拟远程调用等待结果的过程，所以需要SyncWriteFuture暂存结果
        //get为阻塞调用，因此可以使得外部只有等到Response返回后才能获取到结果，这样一定可以拿到Response
        Response response = syncWriteFuture.get(timeout, TimeUnit.SECONDS);
        //没有拿到response，说明请求过程中出错，可能因为超时，也可能因为其他异常
        if (response == null) {
            SyncWriteMap.CLIENT_CACHE.remove(message.getRequestId());
            //这里对于超时异常和其他异常分开处理
            if (syncWriteFuture.isTimeout()) {
                throw new TimeoutException("请求超时，请重试");
            } else {
                throw new NettyResponseException(syncWriteFuture.cause());
            }
        }
        return response;
    }
}
