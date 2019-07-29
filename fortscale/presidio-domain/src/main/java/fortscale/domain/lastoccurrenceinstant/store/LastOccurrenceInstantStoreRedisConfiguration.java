package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.utils.redis.RedisConfiguration;
import fortscale.utils.redis.RedisSerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Map;

@Configuration
@Import(RedisConfiguration.class)
public class LastOccurrenceInstantStoreRedisConfiguration {
    private final JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    public LastOccurrenceInstantStoreRedisConfiguration(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Map<String, Instant>> redisTemplate() {
        RedisTemplate<String, Map<String, Instant>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializers.getStringRedisSerializer());
        redisTemplate.setHashKeySerializer(RedisSerializers.getStringRedisSerializer());
        redisTemplate.setHashValueSerializer(RedisSerializers.getInstantRedisSerializer());
        return redisTemplate;
    }

    @Bean("lastOccurrenceInstantStoreRedis")
    public LastOccurrenceInstantStoreRedisImpl lastOccurrenceInstantStoreRedis() {
        return new LastOccurrenceInstantStoreRedisImpl(redisTemplate());
    }
}
