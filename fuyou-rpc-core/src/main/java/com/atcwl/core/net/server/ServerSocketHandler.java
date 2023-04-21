package com.atcwl.core.net.server;

import com.atcwl.common.cache.SimpleRpcServiceCache;
import com.atcwl.common.constrant.CommonConstant;
import com.atcwl.common.constrant.enums.CompressType;
import com.atcwl.common.constrant.enums.MessageType;
import com.atcwl.common.constrant.enums.SerializerType;
import com.atcwl.common.exception.network.NettyInitException;
import com.atcwl.core.filter.impl.FilterInvoke;
import com.atcwl.core.filter.impl.SpiLoadFilter;
import com.atcwl.core.net.message.FuyouRpcMessage;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import com.atcwl.core.net.send.SyncWriteMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class ServerSocketHandler extends SimpleChannelInboundHandler<FuyouRpcMessage> {
    private ExecutorService dealReqThreadPool = Executors.newFixedThreadPool(10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FuyouRpcMessage message) throws Exception {
        try {
            if (message.getMessageType() != MessageType.REQUEST.getType()) {
                return ;
            }
            Request request = (Request) message.getData();
            //缓存本次请求，该缓存只是暂时的，请求处理完成后就会移除
            SyncWriteMap.SERVER_CACHE.put(request.getRequestId(), Thread.currentThread());
            Class<?> interType = Class.forName(request.getInterfaceName());
            Class[] parameterTypes = request.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length <= 0) {
                throw new RuntimeException("调用方法参数类型异常");
            }
            Method method = interType.getMethod(request.getMethodName(), parameterTypes);
            //这里是对应服务对象的Key，我们在将对应接口实=实现类对象注册到本地注册表时，会以这样形式的key进行注册
            //这里的key规范是由框架本身决定，也是由我们来决定的
            String registerKey = CommonConstant.RPC_SERVICE_PREFIX + "_"
                    + request.getInterfaceName() + "_" + request.getAlias();
            Object bean = SimpleRpcServiceCache.getService(registerKey);
            //加载过滤器
            SpiLoadFilter.loadFilters();
            //执行远程调用前置过滤器，设置上下文信息，同时更新Span信息
            request.setFuyouRpcContext(FilterInvoke.loadRemoteInvokeBeforeFilters(request.getFuyouRpcContext()));
            //这里是将请求交给线程池中的线程去异步处理了，这样可以提高服务端接受请求的能力
            //因为请求的具体逻辑是异步处理的，所以主线程可以专门接受请求
            dealReqThreadPool.submit(() -> asyncRealInvoke(ctx, message, request, method, bean));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void asyncRealInvoke(ChannelHandlerContext ctx, FuyouRpcMessage message, Request request, Method method, Object bean) {
        Object result = null;
        try {
            result = method.invoke(bean, request.getParameters());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (result == null) {
            throw new NettyInitException("真实调用异常");
        }
        //得到结果后，准备响应
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        response.setData(result);
        //Message为统一消息格式，所以在通信前都必须封装Message
        FuyouRpcMessage res = new FuyouRpcMessage();
        message.setRequestId(request.getRequestId());
        message.setMessageType(MessageType.RESPONSE.getType());
        message.setData(response);
        message.setSerializeType(SerializerType.fromName(request.getSerializer()).getType());
        message.setCompressType(CompressType.fromName(request.getCompressor()).getType());
        //设置好响应后写出
        ctx.writeAndFlush(res)
                .addListener(future -> {
                    //不管有没有写出成功，服务器只要响应了，就应该删除对应的缓存，否则可能出现一个线程同时处理多个请求的问题
                    SyncWriteMap.SERVER_CACHE.remove(request.getRequestId());
                });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //空闲状态连接，能够触发该事件说明连接空闲达到了最大时间限制，这时需要断开连接
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
