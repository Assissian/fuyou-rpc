package com.atcwl.core.net.message;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class FuyouRpcMessage implements Cloneable {
    /**
     * 消息类型：如心跳消息，普通消息
     */
    private byte messageType;
    /**
     * 序列化类型：提供了复数种序列化机制，如protobuf
     */
    private byte serializeType;
    /**
     * 压缩类型：默认不压缩，但可以选择gzip压缩，或者其他压缩方式
     */
    private byte compressType;
    /**
     * 请求Id
     */
    private long requestId;
    /**
     * 具体数据：可以承载请求数据（Request），或者是相应数据（Response）
     */
    private Object data;

    public FuyouRpcMessage() {
    }

    public FuyouRpcMessage(byte messageType, byte serializeType, byte compressType, long requestId, Object data) {
        this.messageType = messageType;
        this.serializeType = serializeType;
        this.compressType = compressType;
        this.requestId = requestId;
        this.data = data;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public byte getCompressType() {
        return compressType;
    }

    public void setCompressType(byte compressType) {
        this.compressType = compressType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    /**
     * 重写克隆方法：后续可能有复制消息的需求
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected FuyouRpcMessage clone() throws CloneNotSupportedException {
        FuyouRpcMessage messageNew = new FuyouRpcMessage();
        messageNew.setMessageType(this.messageType);
        messageNew.setSerializeType(this.serializeType);
        messageNew.setCompressType(this.compressType);
        messageNew.setRequestId(this.requestId);
        return messageNew;
    }
}
