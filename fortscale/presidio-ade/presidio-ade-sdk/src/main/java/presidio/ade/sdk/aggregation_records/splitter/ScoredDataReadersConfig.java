package presidio.ade.sdk.aggregation_records.splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.ScoredDataReader;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;

@Configuration
@Import({
        ScoredEnrichedDataStoreMongoConfig.class,
        AggregatedDataStoreConfig.class
})
public class ScoredDataReadersConfig {
    private final ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader;
    private final ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader;

    @Autowired
    @SuppressWarnings("unchecked")
    public ScoredDataReadersConfig(
            ScoredEnrichedDataStore scoredEnrichedDataStore,
            AggregatedDataStore aggregatedDataStore) {

        scoredEnrichedDataReader = (ScoredDataReader<AdeScoredEnrichedRecord>)scoredEnrichedDataStore;
        scoredFeatureAggregationDataReader = (ScoredDataReader<ScoredFeatureAggregationRecord>)aggregatedDataStore;
    }

    @Bean
    public ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader() {
        return scoredEnrichedDataReader;
    }

    @Bean
    public ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader() {
        return scoredFeatureAggregationDataReader;
    }
}
