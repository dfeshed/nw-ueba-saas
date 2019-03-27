package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregatorConfig;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.RecordReaderFactoryServiceConfig;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.ScoredDataReader;

@Configuration
@Import({
        RecordReaderFactoryServiceConfig.class,
        InMemoryFeatureBucketAggregatorConfig.class,
        ScoredDataReadersConfig.class
})
public class ScoreAggregationRecordSplitterConfig {
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private final ScoredDataReaderViewerSwitch scoredDataReaderViewerSwitch;
    private final int scoreAggregationRecordContributorsLimit;

    @Autowired
    public ScoreAggregationRecordSplitterConfig(
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            ScoredDataReader<AdeScoredEnrichedRecord> scoredEnrichedDataReader,
            ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader,
            @Value("${presidio.ade.sdk.scored.record.score.threshold:0}") int scoredRecordScoreThreshold,
            @Value("${presidio.ade.sdk.scored.enriched.record.page.size:10000}") int scoredEnrichedRecordPageSize,
            @Value("${presidio.ade.sdk.scored.feature.aggregation.record.page.size:10000}") int scoredFeatureAggregationRecordPageSize,
            @Value("${presidio.ade.sdk.score.aggregation.record.contributors.limit:25}") int scoreAggregationRecordContributorsLimit) {

        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.scoreAggregationRecordContributorsLimit = scoreAggregationRecordContributorsLimit;

        scoredDataReaderViewerSwitch = new ScoredDataReaderViewerSwitch();
        scoredDataReaderViewerSwitch.add(AdeScoredEnrichedRecord.class, new ScoredDataReaderViewer<>(
            scoredEnrichedDataReader, scoredRecordScoreThreshold, scoredEnrichedRecordPageSize));
        scoredDataReaderViewerSwitch.add(ScoredFeatureAggregationRecord.class, new ScoredDataReaderViewer<>(
            scoredFeatureAggregationDataReader, scoredRecordScoreThreshold, scoredFeatureAggregationRecordPageSize));
    }

    @Bean
    public ScoreAggregationRecordSplitter scoreAggregationRecordSplitter() {
        return new ScoreAggregationRecordSplitter(
                aggregatedFeatureEventsConfService,
                recordReaderFactoryService,
                inMemoryFeatureBucketAggregator,
                scoredDataReaderViewerSwitch,
                scoreAggregationRecordContributorsLimit);
    }
}
