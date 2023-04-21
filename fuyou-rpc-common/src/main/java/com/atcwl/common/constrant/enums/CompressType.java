package com.atcwl.common.constrant.enums;

/**
 * 指定压缩类型的枚举类：包含 不压缩 以及 GZIP 压缩
 * @Author cwl
 * @date
 * @apiNote
 */
public enum CompressType {
    DEFAULT((byte) 1, "no_compress"),
    GZIP((byte) 2, "gzip");

    private final byte type;
    private final String name;

    CompressType(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public byte getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static CompressType fromType(byte type) {
        for(CompressType compressType : CompressType.values()) {
            if (compressType.type == type) {
                return compressType;
            }
        }
        return DEFAULT;
    }

    public static CompressType fromName(String name) {
        for(CompressType compressType : CompressType.values()) {
            if (compressType.name.equals(name)) {
                return compressType;
            }
        }
        return DEFAULT;
    }
}
