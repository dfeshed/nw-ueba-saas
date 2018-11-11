package presidio.ade.processes.shell.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.monitoring.flush.MetricContainerFlusher;

import java.util.List;
import java.util.stream.Collectors;

public class FeatureAggregationService extends FixedDurationStrategyExecutor {
    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    private FeatureAggregationScoringService featureAggregationScoringService;
    private AggregationRecordsCreator featureAggregationsCreator;
    private AggregatedDataStore scoredFeatureAggregatedStore;
    private int pageSize;
    private int maxGroupSize;
    private MetricContainerFlusher metricContainerFlusher;
    private LevelThreeAggregationsService levelThreeAggregationsService;

    public FeatureAggregationService(
            FixedDurationStrategy fixedDurationStrategy,
            BucketConfigurationService bucketConfigurationService,
            EnrichedDataStore enrichedDataStore,
            InMemoryFeatureBucketAggregator featureBucketAggregator,
            FeatureAggregationScoringService featureAggregationScoringService,
            AggregationRecordsCreator featureAggregationsCreator,
            AggregatedDataStore scoredFeatureAggregatedStore,
            int pageSize,
            int maxGroupSize,
            MetricContainerFlusher metricContainerFlusher,
            LevelThreeAggregationsService levelThreeAggregationsService) {

        super(fixedDurationStrategy);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.metricContainerFlusher = metricContainerFlusher;
        this.levelThreeAggregationsService = levelThreeAggregationsService;
    }

    @Override
    protected void executeSingleTimeRange(
            TimeRange timeRange,
            String adeEventType,
            String contextType,
            List<String> contextFieldNamesToExclude,
            StoreMetadataProperties storeMetadataProperties) {

        // Once a model is saved to the cache, the service will never update the cache again with a newer model.
        // Resetting the cache is required in order to get newer models in each partition and not use older models.
        // If this line is deleted, the model cache will need to have some efficient refresh mechanism.
        featureAggregationScoringService.resetModelCache();

        // For now we don't have multiple contexts, so we just pass a list of size 1.
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);
        FeatureBucketStrategyData featureBucketStrategyData = createFeatureBucketStrategyData(timeRange);

        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBuckets = featureBucketAggregator.aggregate(pageIterator, adeEventType, contextType, contextFieldNamesToExclude, featureBucketStrategyData);
            List<AdeAggregationRecord> featureAdeAggrRecords = featureAggregationsCreator.createAggregationRecords(featureBuckets);
            List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = featureAggregationScoringService.scoreEvents(featureAdeAggrRecords, timeRange);
            scoredFeatureAggregatedStore.store(scoredFeatureAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION, storeMetadataProperties);
            levelThreeAggregationsService.consume(scoredFeatureAggregationRecords, contextType, contextFieldNamesToExclude, featureBucketStrategyData, storeMetadataProperties);
        }

        // Flush stored metrics to elasticsearch.
        metricContainerFlusher.flush();
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange) {
        String strategyName = strategy.toStrategyName();
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
    }

    @Override
    protected List<List<String>> getListsOfContextFieldNames(String adeEventType, FixedDurationStrategy strategy) {
        List<FeatureBucketConf> featureBucketConfList = bucketConfigurationService.getFeatureBucketConfs(adeEventType, strategy.toStrategyName());
        return featureBucketConfList.stream().map(FeatureBucketConf::getContextFieldNames).collect(Collectors.toList());
    }
}
