package com.atcwl.core.net.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.core.net.message.Request;
import io.netty.channel.ChannelFuture;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接缓存，每次建立连接后，就会将对应的channelFuture缓存
 * 这里缓存key是使用的对端地址的URL作为key
 * @Author cwl
 * @date
 * @apiNote
 */
public class ConnectCache {
    private static final Map<String, ChannelFuture> CONNECT_CACHE = new ConcurrentHashMap<>(16);

    /**
     * 这里是建立连接缓存
     * 对于连接缓存，一个进程最多只能和服务端有一个连接，多线程无法区分端口，所以多个线程无法建立多个连接
     * 所以对于连接，我们需要避免重复建立，因此在建立连接缓存是需要判断：只有第一次连接时需要缓存，后续复用该连接即可
     * 这里如果是不同的进程是不影响的，虽然我们以服务端URL为key建立连接缓存
     * 但不同进程的内存空间不共享，所以它们的连接缓存也是相互独立的，并不会造成多个进程复用一个连接的问题
     * @param request
     * @return
     */
    public static boolean saveChannelFuture(Request request) {
        if (Objects.isNull(request)) {
            return false;
        }
        String url = request.getHost() + "_" + request.getPort();
        ChannelFuture cache = CONNECT_CACHE.get(url);
        ChannelFuture channelFuture = request.getChannelFuture();
        if (Objects.isNull(cache) && !Objects.isNull(channelFuture) && channelFuture.channel().isOpen()) {
            CONNECT_CACHE.put(url, channelFuture);
            return true;
        }
        return false;
    }

    /**
     * 获取缓存的连接
     * 由于使用了长连接，我们通过缓存ChannelFuture，使得客户端的多个线程可以复用同一连接
     * @param request
     * @return
     */
    public static ChannelFuture getChannelFuture(Request request) {
        if (Objects.isNull(request)) {
            return null;
        }
        String url = request.getHost() + "_" + request.getPort();
        return CONNECT_CACHE.get(url);
    }

    /**
     * 移除指定url的服务端连接
     * 这个方法往往在连接因为某些原因中断后由相关的监听器调用，连接断开后，我们需要将该连接从缓存中删除
     * @param urls
     * @return
     */
    public static boolean remove(List<String>urls) {
        if (CollectionUtil.isEmpty(urls)) {
            return false;
        }
        for (String url : urls) {
            ChannelFuture channelFuture = CONNECT_CACHE.get(url);
            channelFuture.channel().close();
            CONNECT_CACHE.remove(url);
        }
        return true;
    }
}
