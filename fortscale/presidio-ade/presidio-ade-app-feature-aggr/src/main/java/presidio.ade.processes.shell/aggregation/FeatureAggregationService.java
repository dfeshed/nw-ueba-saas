package presidio.ade.processes.shell.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.*;


public class FeatureAggregationService extends FixedDurationStrategyExecutor {

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    private FeatureAggregationScoringService featureAggregationScoringService;
    private AggregationRecordsCreator featureAggregationsCreator;
    private AggregatedDataStore scoredFeatureAggregatedStore;
    private int pageSize;
    private int maxGroupSize;
    private ModelsCacheService modelCacheServiceInMemory;

    public FeatureAggregationService(FixedDurationStrategy fixedDurationStrategy,
                                     BucketConfigurationService bucketConfigurationService,
                                     EnrichedDataStore enrichedDataStore,
                                     InMemoryFeatureBucketAggregator featureBucketAggregator,
                                     FeatureAggregationScoringService featureAggregationScoringService,
                                     AggregationRecordsCreator featureAggregationsCreator,
                                     AggregatedDataStore scoredFeatureAggregatedStore, int pageSize, int maxGroupSize,
                                     ModelsCacheService modelCacheServiceInMemory) {
        super(fixedDurationStrategy);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
        this.modelCacheServiceInMemory = modelCacheServiceInMemory;
    }

    @Override
    protected void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType) {

        modelCacheServiceInMemory.resetCache();

        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = Collections.singletonList(contextType);

        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBuckets = featureBucketAggregator.aggregate(pageIterator, contextTypes, createFeatureBucketStrategyData(timeRange));

            //feature bucket creation
            List<AdeAggregationRecord> featureAdeAggrRecords = featureAggregationsCreator.createAggregationRecords(featureBuckets);

            List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = featureAggregationScoringService.scoreEvents(featureAdeAggrRecords);

            scoredFeatureAggregatedStore.store(scoredFeatureAggregationRecords, AggregatedFeatureType.FEATURE_AGGREGATION);
        }
    }


    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange) {
        String strategyName = "fixed_duration_" + StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
    }

    @Override
    protected List<String> getDistinctContextTypes(String adeEventType) {
        Set<List<String>> distinctMultipleContextsTypeSet = bucketConfigurationService.getRelatedDistinctContexts(adeEventType);
        Set<String> distinctSingleContextTypeSet = new HashSet<>();
        for (List<String> distinctMultipleContexts : distinctMultipleContextsTypeSet) {
            distinctSingleContextTypeSet.addAll(distinctMultipleContexts);
        }
        return new ArrayList<>(distinctSingleContextTypeSet);
    }

}
