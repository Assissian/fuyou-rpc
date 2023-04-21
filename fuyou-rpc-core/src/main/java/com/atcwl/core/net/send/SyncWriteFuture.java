package com.atcwl.core.net.send;

import com.atcwl.core.net.message.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 同步写操作
 * 按照RPC调用的规则，默认情况下RPC调用应该是同步调用，在未返回Response前，方法调用是不能返回的
 * 无论RPC调用是否成功，都只有等到Response到达之后才能判断，因此需要提供阻塞获取功能，来达成同步写功能
 * SyncWriteFuture是一个包装类，本质上主要用于存储响应结果时使用
 * 由于Netty基于NIO，所以它的调用过程大部分都是异步的，这样我们在写完请求后不会阻塞，而是直接进行下一个请求
 * 但这样的设计不符合RPC的原则，因此可以利用Future的特性：存储异步调用结果
 * 当我们写完一个请求后，我们会创建SyncWriteFuture对象，并存储到缓存中（对于一个请求的结果需要缓存，否则可能因为请求切换
 * 导致上一次请求结果丢失），这样当它的响应返回时，我们可以通过缓存拿到对应SyncWriteFuture并装入Response，这样客户端
 * 就可以通过SyncWriteFuture来获取到指定请求Response；同时由于SyncWriteFuture提供了阻塞获取的功能，因此在获取结果时
 * 是同步获取的，不会出现RPC调用获取不到结果的情况
 * @Author cwl
 * @date
 * @apiNote
 */
public class SyncWriteFuture implements WriteFuture<Response> {
    //利用CountDownLatch实现同步，在创建SyncWriteFuture对象时就会使得创建写操作的线程等待响应结果到达后才能返回
    //利用CountDownLatch可以达到阻塞获取的效果，由于没有继承FutureTask类，所以只有自己实现阻塞获取的功能
    private CountDownLatch latch = new CountDownLatch(1);
    //这个起始时间主要用于后续判断写操作是否超时
    private Long beginTime = System.currentTimeMillis();
    private Long timeout;
    private Response response;
    private final Long requestId;
    private boolean writeResult;
    private Throwable cause;
    private boolean isTimeout = false;

    public SyncWriteFuture(Long requestId) {
        this.requestId = requestId;
        this.timeout = 30L;
        writeResult = true;
        isTimeout = false;
    }

    public SyncWriteFuture(long requestId, long timeout) {
        this.requestId = requestId;
        this.timeout =  timeout;
        writeResult = true;
        isTimeout = false;
    }

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public void setCause(Throwable e) {
        this.cause = e;
    }

    @Override
    public boolean isWriteSuccess() {
        return false;
    }

    @Override
    public Response response() {
        return response;
    }

    @Override
    public void setResponse(Response response) {
        //这里获取到了Response，可以让外部操作返回了，这里不必等待子线程返回Response了，已经拿到了
        this.response = response;
        //释放等待的主线程，让其能够处理Response
        latch.countDown();
    }

    @Override
    public long requestId() {
        return this.requestId;
    }

    @Override
    public void setWriteResult(boolean writeResult) {
        this.writeResult = writeResult;
    }

    @Override
    public boolean isTimeout() {
        return this.isTimeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    /**
     * 凡是Response获取操作，需要等到Response到达，因此获取之前调用latch.await
     * 等到Response被设置后就会返回，从而完成同步写
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Response get() throws InterruptedException, ExecutionException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.response;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return this.response;
        }
        return null;
    }
}
