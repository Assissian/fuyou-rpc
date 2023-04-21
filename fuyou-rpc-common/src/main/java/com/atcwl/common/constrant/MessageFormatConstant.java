package com.atcwl.common.constrant;

import cn.hutool.core.util.ByteUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public interface MessageFormatConstant {
    /**
     * 魔数
     */
    byte[] MAGIC = ByteUtil.numberToBytes((short) 0x52ff);
    /**
     * 版本号
     */
    int VERSION = 1;
    /**
     * 请求ID生成器
     */
    AtomicLong REQUEST_ID = new AtomicLong(0);
    /**
     * 心跳数据相关
     */
    String PING = "ping";
    String PONG = "pong";
    /**
     * 魔数部分长度
     */
    int MAGIC_LENGTH = 2;
    /**
     * 版本部分长度
     */
    int VERSION_LENGTH = 1;
    /**
     * 消息全长部分的长度
     */
    int FULL_LENGTH = 4;
    /**
     * 消息类型部分长度
     */
    int MESSAGE_TYPE_LENGTH = 1;
    /**
     * 序列化方式部分长度
     */
    int CODEC_LENGTH = 1;
    /**
     * 压缩方式部分长度
     */
    int COMPRESS_TYPE_LENGTH = 1;
    /**
     * 请求消息头部长度
     */
    int HEADER_LENGTH =
            MAGIC_LENGTH + VERSION_LENGTH + FULL_LENGTH + MESSAGE_TYPE_LENGTH + CODEC_LENGTH + COMPRESS_TYPE_LENGTH;
    /**
     * 最大消息体长度：8M
     */
    int MAX_FRAME_LENGTH = 8*1024*1024;
}
