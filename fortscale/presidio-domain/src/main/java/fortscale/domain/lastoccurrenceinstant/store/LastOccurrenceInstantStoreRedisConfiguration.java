package fortscale.domain.lastoccurrenceinstant.store;

import fortscale.utils.redis.RedisConfiguration;
import fortscale.utils.redis.RedisSerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.Instant;

@Configuration
@Import(RedisConfiguration.class)
public class LastOccurrenceInstantStoreRedisConfiguration {
    private final JedisConnectionFactory jedisConnectionFactory;
    private final Duration timeout;

    @Autowired
    public LastOccurrenceInstantStoreRedisConfiguration(
            JedisConnectionFactory jedisConnectionFactory,
            // Default timeout duration is 6 months.
            @Value("#{T(java.time.Duration).parse('${presidio.last.occurrence.instant.store.redis.timeout:P180D}')}") Duration timeout) {

        this.jedisConnectionFactory = jedisConnectionFactory;
        this.timeout = timeout;
    }

    @Bean
    public RedisTemplate<String, Instant> redisTemplate() {
        RedisTemplate<String, Instant> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializers.getStringRedisSerializer());
        redisTemplate.setValueSerializer(RedisSerializers.getInstantRedisSerializer());
        return redisTemplate;
    }

    @Bean("lastOccurrenceInstantStoreRedis")
    public LastOccurrenceInstantStoreRedisImpl lastOccurrenceInstantStoreRedis() {
        return new LastOccurrenceInstantStoreRedisImpl(redisTemplate(), timeout);
    }
}
