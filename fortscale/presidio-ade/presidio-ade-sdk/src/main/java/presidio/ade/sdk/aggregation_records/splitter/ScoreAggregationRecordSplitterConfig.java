package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.ScoredDataReader;

@Configuration
public class ScoreAggregationRecordSplitterConfig {
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private final ScoredRecordPageIteratorFactory scoredRecordPageIteratorFactory;

    @Autowired
    public ScoreAggregationRecordSplitterConfig(
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader,
            ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader,
            @Value("${presidio.ade.sdk.scored.enriched.record.page.size:10000}") int scoredEnrichedRecordPageSize,
            @Value("${presidio.ade.sdk.scored.feature.aggregation.record.page.size:10000}") int scoredFeatureAggregationRecordPageSize) {

        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.scoredRecordPageIteratorFactory = new ScoredRecordPageIteratorFactory(
                scoredEnrichedDataReader,
                scoredFeatureAggregationDataReader,
                scoredEnrichedRecordPageSize,
                scoredFeatureAggregationRecordPageSize);
    }

    @Bean
    public ScoreAggregationRecordSplitter scoreAggregationRecordSplitter() {
        return new ScoreAggregationRecordSplitter(
                aggregatedFeatureEventsConfService,
                recordReaderFactoryService,
                inMemoryFeatureBucketAggregator,
                scoredRecordPageIteratorFactory);
    }
}
