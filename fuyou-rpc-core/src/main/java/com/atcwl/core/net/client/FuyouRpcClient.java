package com.atcwl.core.net.client;

import com.atcwl.core.net.codec.MessageDecoder;
import com.atcwl.core.net.codec.MessageEncoder;
import com.atcwl.core.net.message.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 建立客户端，本类的作用主要是建立netty客户端，主要是建立连接用的
 * 处理交给handler来完成
 * @Author cwl
 * @date
 * @apiNote
 */
public class FuyouRpcClient implements Runnable {
    /**
     * channelFuture作用是给外界提供客户端操作功能
     * rpcClient的作用主要是建立客户端连接，但是建立连接后，我们需要对通道进行操作，否则没法进行通信
     * channelFuture属性就起到了关键作用，保存了通道
     */
    private ChannelFuture channelFuture;

    private final String host;
    private final int port;

    private final Request request;

    public FuyouRpcClient(Request request) {
        this.request = request;
        this.host = request.getHost();
        this.port = request.getPort();
    }

    @Override
    public void run() {
        //计算心跳时间
        Long heartBeatTime = Objects.isNull(request.getBeatIntervalTime()) ? 10 : request.getBeatIntervalTime();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //构建netty的client端
            Bootstrap client = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(
                                            new IdleStateHandler(0, heartBeatTime, 0, TimeUnit.SECONDS),
                                            new MessageDecoder(),
                                            new MessageEncoder(),
                                            new ClientSocketHandler()
                                    );
                        }
                    });
            InetSocketAddress address = new InetSocketAddress(host, port);
            //这里需要同步等待连接建立，否则无法发送数据，为了避免外部使用错误，最好同步
            ChannelFuture f = client.connect(address).sync();
            this.channelFuture = f;
            //这里必须同步，不能使用closeFuture()，closeFuture是异步关闭，而我们需要维持TCP长连接，关闭时需要进行处理
            f.channel().close().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }
}
