package com.atcwl.common.constrant.enums;

/**
 * 指定序列化类型：这里包含protostuff和默认两种类型
 * @Author cwl
 * @date
 * @apiNote
 */
public enum SerializerType {
    PROTOSTUFF((byte) 1, "protostuff"),
    SPI((byte) 2, "serializer");

    private final byte type;
    private final String name;

    SerializerType(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public byte getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static SerializerType fromName(String name) {
        for(SerializerType serializerType : SerializerType.values()) {
            if (serializerType.name.equals(name)) {
                return serializerType;
            }
        }
        return PROTOSTUFF;
    }

    public static SerializerType fromType(byte type) {
        for(SerializerType serializerType : SerializerType.values()) {
            if (serializerType.type == type) {
                return serializerType;
            }
        }
        return PROTOSTUFF;
    }
}
