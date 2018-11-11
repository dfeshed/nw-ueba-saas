package presidio.ade.processes.shell.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.store.record.StoreMetadataProperties;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;

import java.util.List;

public class LevelThreeAggregationsService {
    private final FeatureBucketService<ScoredFeatureAggregationRecord> featureBucketService;
    private final AggregationRecordsCreator aggregationRecordsCreator;
    private final AggregatedDataStore aggregatedDataStore;

    public LevelThreeAggregationsService(
            FeatureBucketService<ScoredFeatureAggregationRecord> featureBucketService,
            AggregationRecordsCreator aggregationRecordsCreator,
            AggregatedDataStore aggregatedDataStore) {

        this.featureBucketService = featureBucketService;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.aggregatedDataStore = aggregatedDataStore;
    }

    public void consume(
            List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords,
            String contextFieldName,
            List<String> contextFieldNamesToExclude,
            FeatureBucketStrategyData featureBucketStrategyData,
            StoreMetadataProperties storeMetadataProperties) {

        if (!scoredFeatureAggregationRecords.isEmpty()) {
            featureBucketService.updateFeatureBuckets(scoredFeatureAggregationRecords, contextFieldName, contextFieldNamesToExclude, featureBucketStrategyData);
            List<FeatureBucket> featureBuckets = featureBucketService.closeFeatureBuckets();
            List<AdeAggregationRecord> scoreAggregationRecords = aggregationRecordsCreator.createAggregationRecords(featureBuckets);
            aggregatedDataStore.store(scoreAggregationRecords, AggregatedFeatureType.SCORE_AGGREGATION, storeMetadataProperties);
        }
    }
}
