package com.atcwl.core.net.client;

import com.atcwl.core.net.cache.ConnectCache;
import io.netty.channel.ChannelFutureListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public interface RemoveChannelFutureListener  {
    ChannelFutureListener BEAT_ON_REMOVE_CHANNEL =future -> {
        //如果发送心跳数据包后没有得到响应，那么说明连接不可用，需断开连接
        if (!future.isSuccess()) {
            InetSocketAddress address = (InetSocketAddress) future.channel().remoteAddress();
            String host = address.getHostString();
            int port = address.getPort();
            String url = host + "_" + port;
            List<String> urls = Arrays.asList(url);
            ConnectCache.remove(urls);
        }
    };
}
