package com.atcwl.common.interfaces;

import com.atcwl.common.annotation.FuyouRpcSPI;

/**
 * 序列化接口
 * @Author cwl
 * @date
 * @apiNote
 */
@FuyouRpcSPI("protostuff")
public interface Serializer {
    /**
     * 序列化
     * @param target
     * @return
     */
    byte[] serialize(Object target);

    /**
     * 反序列化
     * @param target
     * @return
     */
    <T> T deserialize(byte[] target, Class<T> targetType);
}
