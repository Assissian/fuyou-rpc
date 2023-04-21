package com.atcwl.core.net.message;

import io.netty.channel.Channel;
/**
 * 自定义响应类型，与Request对应
 * @Author cwl
 * @date
 * @apiNote
 */
public class Response {
    /**
     *  保存对应通道信息，缓存返回值时方便使用（Server端正确返回响应后需要删除对应请求缓存）
     */
    private transient Channel channel;
    /**
     * 请求ID：标识该响应是哪一个请求的响应，在高并发、大流量的系统种该属性是必要的
     */
    private long requestId;
    /**
     * 保存返回数据
     */
    private Object data;
    /**
     * 保存异常信息，正常返回时为null
     */
    private Object exceptionInfo;

    public Response() {
    }

    public Response(Channel channel, long requestId, Object data, Object exceptionInfo) {
        this.channel = channel;
        this.requestId = requestId;
        this.data = data;
        this.exceptionInfo = exceptionInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(Object exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }
}
