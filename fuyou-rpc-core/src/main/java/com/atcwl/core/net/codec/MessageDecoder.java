package com.atcwl.core.net.codec;

import com.atcwl.common.interfaces.Compressor;
import com.atcwl.common.constrant.MessageFormatConstant;
import com.atcwl.common.interfaces.Serializer;
import com.atcwl.common.constrant.enums.CompressType;
import com.atcwl.common.constrant.enums.MessageType;
import com.atcwl.common.constrant.enums.SerializerType;
import com.atcwl.common.spi.ExtensionLoader;
import com.atcwl.core.net.message.FuyouRpcMessage;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Arrays;

/**
 * 消息解码器，对于传入进来的RPC消息需要按照协议格式进行解码
 * 这里我们使用自定义的协议格式，结合LengthFieldBasedFrameDecoder，因此不用担心粘包、半包的问题
 * @Author cwl
 * @date
 * @apiNote
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    public MessageDecoder() {
        //初始化解码器，按照指定规则
        super(
                //确定解码报文允许的最大长度，超过该长度的报文直接丢弃
                MessageFormatConstant.MAX_FRAME_LENGTH,
                //指定全长字段的起始位置
                MessageFormatConstant.MAGIC_LENGTH + MessageFormatConstant.VERSION_LENGTH,
                //指定长度字段的字节数
                MessageFormatConstant.FULL_LENGTH,
                // LengthFieldBasedFrameDecoder 拿到消息长度之后，还会加上 [4B full length（消息长度）] 字段前面的长度
                // 因为我们的消息长度包含了这部分了（刚才指定的三个部分：魔数，版本，以及长度，他会跳过该长度的字段）
                // 我们的报文长度中包含它跳过的这三个部分，所以需要减回去
                -(MessageFormatConstant.MAGIC_LENGTH + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.FULL_LENGTH),
                //指定需要丢弃头部的多少个字节的数据
                0
        );
    }

    /**
     * 解码报文
     * @return 返回一个FuyouRpcMessage消息
     * @throws Exception
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
         Object decode = super.decode(ctx, in);
         if (decode instanceof ByteBuf) {
             //这里获得ByteBuf说明数据未被转换成无法处理的数据，依然是字节数据，那么我们可以将其恢复
             ByteBuf buf = (ByteBuf) decode;
             if (((ByteBuf) decode).readableBytes() > 0) {
                 try {
                     return decodeFrame(buf);
                 } catch (Exception e) {
                     System.out.println("解码异常");
                 } finally {
                     //这里处理完成后需要释放ByteBuf，从而避免后续流水线上的其他入栈处理器进行其他处理，造成数据错误
                        buf.release();
                 }
             }
         }
         //走到这里，说明得到的字节数据被前面的其他处理器转换了，那么说明不是需要的消息数据
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        //检查魔数和版本是否正确
        boolean magicRight = verifyMagicNumber(in);
        boolean versionRight = verifyVersion(in);
        if (magicRight || versionRight) {
            throw new IllegalArgumentException(" 非法的请求");
        }
        int fullLen = in.readInt();
        //获得数据长度：总长-Header长度
        int bodyLen = fullLen - MessageFormatConstant.HEADER_LENGTH;
        //构建返回消息
        FuyouRpcMessage result = new FuyouRpcMessage();
        byte messageType = in.readByte();
        result.setMessageType(messageType);
        byte serializeType = in.readByte();
        result.setSerializeType(serializeType);
        byte compressType = in.readByte();
        result.setCompressType(compressType);
        long requestId = in.readLong();
        result.setRequestId(requestId);

        //读取实际数据
        byte[] data = new byte[bodyLen];
        in.readBytes(data);
        //将消息反序列化为对象并封装到Message中
        getData(data, result);
        return result;
    }

    private void getData(byte[] data, FuyouRpcMessage message) {
        //心跳消息没有数据，可以直接跳过
        byte messageType = message.getMessageType();
        if (messageType == MessageType.HEARTBEAT.getType()) {
            return ;
        }
        //接下来就是对于反序列化和解压缩的处理
        SerializerType serializerType = SerializerType.fromType(message.getSerializeType());
        if (serializerType == null) {
            throw new IllegalArgumentException("不存在该序列化器");
        }
        Serializer serializer =
                ExtensionLoader.getLoader(Serializer.class).getExtension(serializerType.getName());
        CompressType compressType = CompressType.fromType(message.getCompressType());
        if (compressType == null) {
            throw new IllegalArgumentException("不存在该压缩方式");
        }
        Compressor compressor =
                ExtensionLoader.getLoader(Compressor.class).getExtension(compressType.getName());

        byte[] noCompressed = compressor.uncompress(data);
        //这里我们会将数据转换成指定Request或Response
        Class<?> clazz = message.getMessageType() == MessageType.REQUEST.getType() ? Request.class  : Response.class;
        Object res = serializer.deserialize(noCompressed, clazz);
        message.setData(res);
    }

    private boolean verifyMagicNumber(ByteBuf in) {
        byte[] magic = new byte[MessageFormatConstant.MAGIC_LENGTH];
        in.readBytes(magic);
        return Arrays.equals(magic, MessageFormatConstant.MAGIC);
    }

    private boolean verifyVersion(ByteBuf in) {
        byte version = in.readByte();
        return version == MessageFormatConstant.VERSION;
    }
}
