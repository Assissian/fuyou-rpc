package com.atcwl.core.net.codec;

import com.atcwl.common.interfaces.Compressor;
import com.atcwl.common.constrant.MessageFormatConstant;
import com.atcwl.common.interfaces.Serializer;
import com.atcwl.common.constrant.enums.CompressType;
import com.atcwl.common.constrant.enums.MessageType;
import com.atcwl.common.constrant.enums.SerializerType;
import com.atcwl.common.spi.ExtensionLoader;
import com.atcwl.core.net.message.FuyouRpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Objects;

/**
 * 传输层采用TCP协议，为面向字节流传输，因此所有消息数据都需要转换成字节流
 * 因此采用MessageToByteEncoder
 * @Author cwl
 * @date
 * @apiNote
 */
public class MessageEncoder extends MessageToByteEncoder<FuyouRpcMessage> {
    public MessageEncoder() {
        super();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FuyouRpcMessage fuyouRpcMessage, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        //对于报文长度，我们需要先统计数据部分长度，然后才能统计总长，因此这一部分先跳过，后面来更新
        byteBuf.writerIndex(MessageFormatConstant.MAGIC_LENGTH + MessageFormatConstant.VERSION_LENGTH);
        byteBuf.writeByte(fuyouRpcMessage.getMessageType());
        byteBuf.writeByte(fuyouRpcMessage.getSerializeType());
        byteBuf.writeByte(fuyouRpcMessage.getCompressType());
        byteBuf.writeLong(fuyouRpcMessage.getRequestId());

        int body = writeBody(byteBuf, fuyouRpcMessage);
        int writeIndex = byteBuf.writerIndex();
        // 将写指针移动到报文长度前面，跳过魔数长度和版本长度
        byteBuf.writerIndex(MessageFormatConstant.MAGIC_LENGTH + MessageFormatConstant.VERSION_LENGTH);
        //写入报文长度
        byteBuf.writeInt(body);
        //恢复写指针，否则后续传输数据会有问题（以写指针为结尾的）
        byteBuf.writerIndex(writeIndex);
    }

    /**
     * 写报文数据
     * 这里涉及到报文数据的序列化、压缩等操作，较为复杂
     * @param out
     * @param message
     * @return
     */
    private int writeBody(ByteBuf out, FuyouRpcMessage message) {
        Object data = message.getData();
        if (Objects.isNull(data)) {
            return 0;
        }
        //首先判断消息类型，若为心跳消息，则无需携带数据
         byte messageType = message.getMessageType();
        if (messageType == MessageType.HEARTBEAT.getType()) {
            return 0;
        }
        //确定非心跳消息后，那么接下来需要找到Message指定的序列化器
        SerializerType serializerType = SerializerType.fromType(messageType);
        if (serializerType == null) {
            throw new IllegalArgumentException("没有指定类型的序列化器");
        }
        Serializer serializer =
                ExtensionLoader.getLoader(Serializer.class).getExtension(serializerType.getName());

        //找到Message指定的压缩方式
        CompressType compressType = CompressType.fromType(message.getCompressType());
        if (compressType == null) {
            throw new IllegalArgumentException("没有指定类型的压缩方式");
        }
        Compressor compressor =
                ExtensionLoader.getLoader(Compressor.class).getExtension(compressType.getName());
        //无论是序列化还是压缩，正常情况下一半不会找不到，因为有给默认值（程序或者业务出错就不好说了）
        byte[] noCompressed = serializer.serialize(message.getData());
        byte[] compressed = compressor.compress(noCompressed);

        //写入实体数据，返回其长度
        out.writeBytes(compressed);
        return compressed.length;
    }
}
