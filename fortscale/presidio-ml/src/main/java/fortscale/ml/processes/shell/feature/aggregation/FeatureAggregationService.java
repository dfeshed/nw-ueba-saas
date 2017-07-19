package fortscale.ml.processes.shell.feature.aggregation;

import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.ml.scorer.feature_aggregation_events.FeatureAggregationScoringService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.feature_aggregation_scored.ScoredFeatureAggregatedRecord;
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

    public FeatureAggregationService(FixedDurationStrategy fixedDurationStrategy,
                                     BucketConfigurationService bucketConfigurationService,
                                     EnrichedDataStore enrichedDataStore,
                                     InMemoryFeatureBucketAggregator featureBucketAggregator,
                                     FeatureAggregationScoringService featureAggregationScoringService,
                                     AggregationRecordsCreator featureAggregationsCreator,
                                     AggregatedDataStore scoredFeatureAggregatedStore) {
        super(fixedDurationStrategy);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
        this.featureAggregationScoringService = featureAggregationScoringService;
        this.featureAggregationsCreator = featureAggregationsCreator;
        this.scoredFeatureAggregatedStore = scoredFeatureAggregatedStore;
    }

    @Override
    protected void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = Collections.singletonList(contextType);

        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBuckets = featureBucketAggregator.aggregate(pageIterator, contextTypes, createFeatureBucketStrategyData(timeRange));

            //feature bucket creation
            List<AdeAggregationRecord> featureAdeAggrRecords = featureAggregationsCreator.createAggregationRecords(featureBuckets);

            List<ScoredFeatureAggregatedRecord> scoredFeatureAggregatedRecords = new ArrayList<>();
            featureAggregationScoringService.scoreEvents(scoredFeatureAggregatedRecords, featureAdeAggrRecords);

            scoredFeatureAggregatedStore.store(scoredFeatureAggregatedRecords);
        }
    }


    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange) {
        String strategyName = "fixed_duration_" + StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange.getStart().getEpochSecond(), timeRange.getEnd().getEpochSecond());
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
