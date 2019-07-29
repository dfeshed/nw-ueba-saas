package fortscale.domain.lastoccurrenceinstant.writer;

import fortscale.domain.lastoccurrenceinstant.store.LastOccurrenceInstantStoreRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(LastOccurrenceInstantStoreRedisConfiguration.class)
public class LastOccurrenceInstantWriterCacheConfiguration {
    private final LastOccurrenceInstantWriter lastOccurrenceInstantWriter;
    private final int maximumSize;
    private final double entriesToRemovePercentage;

    @Autowired
    public LastOccurrenceInstantWriterCacheConfiguration(
            @Qualifier("lastOccurrenceInstantStoreRedis") LastOccurrenceInstantWriter lastOccurrenceInstantWriter,
            @Value("${presidio.last.occurrence.instant.writer.maximum.size:10000}") int maximumSize,
            @Value("${presidio.last.occurrence.instant.writer.entries.to.remove.percentage:10.0}") double entriesToRemovePercentage) {

        this.lastOccurrenceInstantWriter = lastOccurrenceInstantWriter;
        this.maximumSize = maximumSize;
        this.entriesToRemovePercentage = entriesToRemovePercentage;
    }

    @Bean("lastOccurrenceInstantWriterCache")
    public LastOccurrenceInstantWriterCacheImpl lastOccurrenceInstantWriterCache() {
        return new LastOccurrenceInstantWriterCacheImpl(
                lastOccurrenceInstantWriter,
                maximumSize, entriesToRemovePercentage);
    }
}
