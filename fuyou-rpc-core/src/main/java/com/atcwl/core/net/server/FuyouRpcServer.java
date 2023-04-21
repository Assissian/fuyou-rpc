package com.atcwl.core.net.server;

import com.atcwl.core.net.codec.MessageDecoder;
import com.atcwl.core.net.codec.MessageEncoder;
import com.atcwl.core.net.message.Request;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * server端，服务应该在启动时就将自身信息注册到注册中心中
 * 因此我们可以在服务启动时，就初始化RpcServer并向注册中心发起注册请求
 * 默认情况下，所有服务启动都是作为server端启动的，但是当你要调用另一个服务时，该服务就会成为client端
 * 一个服务可以同时作为server端和client端，虽然ip和端口相同，但server主要负责接受请求，client主要负责发送请求
 * @Author cwl
 * @date
 * @apiNote
 */
public class FuyouRpcServer implements Runnable {
    private ChannelFuture channelFuture;
    private final Request request;

    public FuyouRpcServer(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        //定义最长空闲时间，即长连接的断开时间
        Long stopIntervalTime = Objects.isNull(request.getStopConnectTime()) || request.getStopConnectTime() <= 0
                    ? 30L : request.getStopConnectTime();
        EventLoopGroup dealConnGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(dealConnGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // 系统用于临时存放已完成三次握手的请求的队列的最大长度。如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 程序进程非正常退出，内核需要一定的时间才能够释放此端口，不设置 SO_REUSEADDR 就无法正常使用该端口。
                    .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    // TCP/IP协议中针对TCP默认开启了Nagle 算法。
                    // Nagle 算法通过减少需要传输的数据包，来优化网络。在内核实现中，数据包的发送和接受会先做缓存，分别对应于写缓存和读缓存。
                    // 启动 TCP_NODELAY，就意味着禁用了 Nagle 算法，允许小包的发送。
                    // 对于延时敏感型，同时数据传输量比较小的应用，开启TCP_NODELAY选项无疑是一个正确的选择
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(
                                            new IdleStateHandler(stopIntervalTime, 0, 0, TimeUnit.SECONDS),
                                            new MessageDecoder(),
                                            new MessageEncoder(),
                                            new ServerSocketHandler()
                                    );
                        }
                    });
            int port = Objects.isNull(request.getPort()) || request.getPort() <= 0 ? 41200 : request.getPort();
            this.channelFuture = serverBootstrap.bind(port);
            this.channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dealConnGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public boolean isActiveServer() {
        try {
            if (channelFuture != null) {
                return (channelFuture.channel().isActive());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }
}
