package fortscale.domain.lastoccurrenceinstant.reader;

import fortscale.domain.lastoccurrenceinstant.store.LastOccurrenceInstantStoreRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(LastOccurrenceInstantStoreRedisConfiguration.class)
public class LastOccurrenceInstantReaderCacheConfiguration {
    private final LastOccurrenceInstantReader lastOccurrenceInstantReader;
    private final int maximumSize;
    private final double entriesToRemovePercentage;

    @Autowired
    public LastOccurrenceInstantReaderCacheConfiguration(
            @Qualifier("lastOccurrenceInstantStoreRedis") LastOccurrenceInstantReader lastOccurrenceInstantReader,
            @Value("${presidio.last.occurrence.instant.reader.maximum.size:10000}") int maximumSize,
            @Value("${presidio.last.occurrence.instant.reader.entries.to.remove.percentage:10.0}") double entriesToRemovePercentage) {

        this.lastOccurrenceInstantReader = lastOccurrenceInstantReader;
        this.maximumSize = maximumSize;
        this.entriesToRemovePercentage = entriesToRemovePercentage;
    }

    @Bean("lastOccurrenceInstantReaderCache")
    public LastOccurrenceInstantReaderCacheImpl lastOccurrenceInstantReaderCache() {
        return new LastOccurrenceInstantReaderCacheImpl(
                lastOccurrenceInstantReader,
                maximumSize, entriesToRemovePercentage);
    }
}
