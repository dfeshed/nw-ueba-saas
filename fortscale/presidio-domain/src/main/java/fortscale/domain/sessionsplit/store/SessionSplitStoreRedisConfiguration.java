package fortscale.domain.sessionsplit.store;

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


@Configuration
@Import(RedisConfiguration.class)
public class SessionSplitStoreRedisConfiguration {

    private final JedisConnectionFactory jedisConnectionFactory;
    private final Duration timeout;

    @Autowired
    public SessionSplitStoreRedisConfiguration(JedisConnectionFactory jedisConnectionFactory,
                                               @Value("#{T(java.time.Duration).parse('${presidio.session.split.store.redis.timeout:P60D}')}") Duration timeout) {
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.timeout = timeout;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializers.getStringRedisSerializer());
        redisTemplate.setHashKeySerializer(RedisSerializers.getStringRedisSerializer());
        redisTemplate.setHashValueSerializer(RedisSerializers.getInstantRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public SessionSplitStoreRedis sessionSplitStoreRedis() {
        return new SessionSplitStoreRedis(redisTemplate(), timeout);
    }
}
