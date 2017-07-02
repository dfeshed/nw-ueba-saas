package fortscale.ml.processes.shell.model.aggregation;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by YaronDL on 7/2/2017.
 */
public class ModelFeatureAggregationBucketsService extends FixedDurationStrategyExecutor {

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator featureBucketAggregator;

    public ModelFeatureAggregationBucketsService(BucketConfigurationService bucketConfigurationService,
                                                 EnrichedDataStore enrichedDataStore, InMemoryFeatureBucketAggregator featureBucketAggregator) {
        super(FixedDurationStrategy.DAILY);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
    }

    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = new ArrayList<>();
        contextTypes.add(contextType);

        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(dataSource, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBucketsToInsert = featureBucketAggregator.aggregate(pageIterator,contextTypes, createFeatureBucketStrategyData(timeRange));
            //todo: save feature buckets to mongo.
        }
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange.getStart().getEpochSecond(), timeRange.getEnd().getEpochSecond());
    }

    @Override
    public List<String> getDistinctContextTypes(String dataSource) {
        Set<List<String>> distinctMultipleContextsTypeSet = bucketConfigurationService.getRelatedDistinctContexts(dataSource);
        Set<String> distinctSingleContextTypeSet = new HashSet<>();
        for(List<String> distinctMultipleContexts: distinctMultipleContextsTypeSet){
            distinctSingleContextTypeSet.addAll(distinctMultipleContexts);
        }
        return new ArrayList<>(distinctSingleContextTypeSet);
    }


}
