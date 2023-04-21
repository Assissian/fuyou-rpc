package com.atcwl.common.constrant.enums;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public enum MessageType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEARTBEAT((byte) 3);

    private final byte type;
    MessageType(byte value) {
        this.type = value;
    }
    public byte getType() {
        return this.type;
    }
}
