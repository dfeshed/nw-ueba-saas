package fortscale.domain.lastoccurrenceinstant;

import fortscale.utils.redis.RedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Import(RedisConfiguration.class)
public class LastOccurrenceInstantWriterConfiguration {
    private final JedisConnectionFactory jedisConnectionFactory;
    private final int maximumSize;
    private final double entriesToRemovePercentage;

    @Autowired
    public LastOccurrenceInstantWriterConfiguration(
            JedisConnectionFactory jedisConnectionFactory,
            @Value("${presidio.last.occurrence.instant.writer.maximum.size:10000}") int maximumSize,
            @Value("${presidio.last.occurrence.instant.writer.entries.to.remove.percentage:10.0}") double entriesToRemovePercentage) {

        this.jedisConnectionFactory = jedisConnectionFactory;
        this.maximumSize = maximumSize;
        this.entriesToRemovePercentage = entriesToRemovePercentage;
    }

    @Bean
    public LastOccurrenceInstantWriter lastOccurrenceInstantWriter() {
        return new LastOccurrenceInstantWriterCacheImpl(
                new LastOccurrenceInstantStoreRedisImpl(jedisConnectionFactory),
                maximumSize, entriesToRemovePercentage);
    }
}
