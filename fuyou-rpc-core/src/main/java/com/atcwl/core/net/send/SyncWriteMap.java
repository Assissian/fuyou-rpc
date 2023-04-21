package com.atcwl.core.net.send;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存工具类，提供client端连接缓存和server端连接缓存
 * @Author cwl
 * @date
 * @apiNote
 */
public class SyncWriteMap {
    /**
     * 客户端连接缓存
     * key 为RequestID
     * value 为对应请求的写操作对象：WriteFuture
     * 这里的客户端缓存主要缓存客户端发出请求调用后的异步调用结果
     * （在netty中，client和server调用是异步的，不缓存结果可能导致请求结果丢失，因为不会阻塞，而是直接进入到下一次操作了）
     * 所以构造了一个特定的Future对象用于存储对应请求的结果，等到客户端需要时可以从中取出对应Future，然后拿到Response
     * 这里客户端需要在后续操作中能够拿到对应Future，就需要依赖于该缓存了
     * 请求完成后就会删除对应的Future
     */
    public static final Map<Long, WriteFuture> CLIENT_CACHE = new HashMap<>();
    /**
     * 服务器对于每一个请求会分配一个线程来处理
     * 因此记录处理相关请求的线程（长连接的原因，同一连接中下一次请求还会由该线程处理
     */
    public static final Map<Long, Thread> SERVER_CACHE = new HashMap<>();
}
