package com.atcwl.core.net.send;

import com.atcwl.core.net.message.Response;

import java.util.concurrent.Future;

/**
 * 异步写接口
 * 继承了Future接口，从而拥有多线程工作的功能
 * 考虑到服务端和客户端都有可能开启多个线程（尤其是服务端，在传输大量数据时）进行写操作
 * 这时为了适配相应操作，开发了WriteFuture接口
 * WriteFuture接口也提供了一些特性方法，比如连接超时判断接口，获取RequestID接口，异常处理接口等
 * @Author cwl
 * @date
 * @apiNote
 */
public interface WriteFuture<T> extends Future<T> {
    /**
     * 获取写过程中出现的异常
     * Future本身作为异步写事件，并没有提供对于异步处理过程中异常的处理，这里是一种扩展
     * 虽然可以使用CompletableFuture来提供更多功能，不过框架还是提供自己的实现更好
     * @return 执行过程出现的异常对象
     */
    Throwable cause();

    /**
     * 设置异常原因
     * @param e
     */
    void setCause(Throwable e);

    /**
     * 判断写操作是否成功
     * 写操作由本框架的工具类来完成，外界需要知道写操作是否成功，因此提供一个返回写操作结果的接口
     * @return
     */
    boolean isWriteSuccess();

    /**
     * 获取响应结果
     * @return
     */
    T response();

    /**
     * 设置响应结果
     * 我们对于响应结果采用了缓存的方式，缓存连接的结果，可以重复使用
     * 这里没有选择直接缓存Response，是因为外部在得到Response后需要进行读取结果数据之类的操作
     * 虽然Response中包含了channelFuture对象，但该对象并不属于本框架的实现，对于用户而言不够友好
     * 所以我们后续会开发一个实现类，用于帮助用户对Response进行操作
     * @param response
     */
    void setResponse(Response response);

    /**
     * 获取响应的RequestID，可以通过请求ID来得到对应连接缓存
     * Response封装在类中，因此该方法主要是为了方便获取请求ID
     * @return
     */
    long requestId();

    /**
     * 设置写操作结果，这个接口主要给我们的工具类调用，用于设置channel写操作的成功与否
     * @param writeResult
     */
    void setWriteResult(boolean writeResult);

    /**
     * 判断是否超时
     * 写操作的时间有超时限制，超过指定时间没有写入数据，连接会自动断开，这时不能再写入数据
     * @return
     */
    boolean isTimeout();
}
