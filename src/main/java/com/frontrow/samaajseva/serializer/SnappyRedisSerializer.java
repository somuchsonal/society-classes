package com.frontrow.samaajseva.serializer;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.xerial.snappy.Snappy;

import java.io.Serializable;

public class SnappyRedisSerializer<T> implements RedisSerializer<T> {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private RedisSerializer<T> innerSerializer;

    public SnappyRedisSerializer() {
    }

    public SnappyRedisSerializer(RedisSerializer<T> innerSerializer) {
        this.innerSerializer = innerSerializer;
    }

    /**
     * Create a byte array by serialising and Compressing a java graph (object)
     */
    @Override
    public byte[] serialize(T object) throws SerializationException {
        try {
            if (object == null)
                return EMPTY_ARRAY;
            byte[] bytes = innerSerializer != null ? innerSerializer.serialize(object)
                    : SerializationUtils.serialize((Serializable) object);
            if (bytes == null || bytes.length == 0)
                return null;
            return Snappy.compress(bytes);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    /**
     * Create a java graph (object) by uncompressing and deserializing a byte array
     */
    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null || bytes.length == 0)
                return null;
            byte[] bos = Snappy.uncompress(bytes);
            return (T) (innerSerializer != null ? innerSerializer.deserialize(bos) : SerializationUtils.deserialize(bos));
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
