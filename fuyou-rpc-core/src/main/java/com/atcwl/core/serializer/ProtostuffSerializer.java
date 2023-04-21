package com.atcwl.core.serializer;

import com.atcwl.common.interfaces.Serializer;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * protostuff序列化器
 * @Author cwl
 * @date
 * @apiNote
 */
public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public byte[] serialize(Object target) {
        Schema schema = RuntimeSchema.getSchema(target.getClass());
        try {
            return ProtostuffIOUtil.toByteArray(target, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] target, Class<T> targetType) {
        Schema<T> schema =RuntimeSchema.getSchema(targetType);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(target, obj, schema);
        return obj;
    }
}
