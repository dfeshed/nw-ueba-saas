package fortscale.domain.lastoccurrenceinstant;

import fortscale.utils.redis.RedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import static org.apache.commons.lang3.Validate.notNull;

@Configuration
@Import(RedisConfiguration.class)
public class LastOccurrenceInstantStoreConfiguration {
    private final JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    public LastOccurrenceInstantStoreConfiguration(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = notNull(jedisConnectionFactory, "jedisConnectionFactory cannot be null.");
    }

    @Bean
    public LastOccurrenceInstantStore lastOccurrenceInstantStore() {
        return new LastOccurrenceInstantStoreRedisImpl(jedisConnectionFactory);
    }
}
