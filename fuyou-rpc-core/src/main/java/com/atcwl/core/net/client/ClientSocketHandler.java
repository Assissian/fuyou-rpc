package com.atcwl.core.net.client;

import com.atcwl.common.constrant.enums.CompressType;
import com.atcwl.common.constrant.enums.MessageType;
import com.atcwl.common.constrant.enums.SerializerType;
import com.atcwl.core.net.message.FuyouRpcMessage;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.net.send.SyncWriteFuture;
import com.atcwl.core.net.send.SyncWriteMap;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *  客户端操作处理器，负责处理客户端的数据
 * @Author cwl
 * @date
 * @apiNote
 */
public class ClientSocketHandler extends SimpleChannelInboundHandler<FuyouRpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FuyouRpcMessage message) throws Exception {
        Response response = (Response) message.getData();
        Long requestId = message.getRequestId();
        SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.CLIENT_CACHE.get(requestId);
        if (future != null) {
            future.setResponse(response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //该类型说明当前响应为心跳事件
            IdleStateEvent event = (IdleStateEvent) evt;
            //由于客户端只设定了写心跳数据包的事件，因此只有当写事件到来时，才会发送心跳包
            if (event.state() == IdleState.WRITER_IDLE) {
                Channel channel = ctx.channel();
                FuyouRpcMessage beat = new FuyouRpcMessage();
                beat.setMessageType(MessageType.HEARTBEAT.getType());
                beat.setSerializeType(SerializerType.PROTOSTUFF.getType());
                beat.setCompressType(CompressType.GZIP.getType());
                channel.writeAndFlush(beat)
                        .addListeners(RemoveChannelFutureListener.BEAT_ON_REMOVE_CHANNEL,
                                ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //当前处理发生异常，则关闭通道，不在进行后续处理
        ctx.close();
        //打印日志
    }
}
