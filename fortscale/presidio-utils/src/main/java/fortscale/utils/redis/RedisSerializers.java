package fortscale.utils.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Instant;

public class RedisSerializers {
    private static final RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
    private static final RedisSerializer<Instant> instantRedisSerializer = new RedisSerializer<Instant>() {
        @Override
        public byte[] serialize(Instant instant) throws SerializationException {
            return instant == null ? null : instant.toString().getBytes();
        }

        @Override
        public Instant deserialize(byte[] bytes) throws SerializationException {
            return bytes == null ? null : Instant.parse(new String(bytes));
        }
    };

    public static RedisSerializer<String> getStringRedisSerializer() {
        return stringRedisSerializer;
    }

    public static RedisSerializer<Instant> getInstantRedisSerializer() {
        return instantRedisSerializer;
    }
}
