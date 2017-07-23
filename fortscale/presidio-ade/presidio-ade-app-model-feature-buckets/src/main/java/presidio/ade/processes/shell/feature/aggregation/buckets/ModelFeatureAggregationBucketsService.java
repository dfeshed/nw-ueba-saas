package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.StringUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.*;

/**
 * Created by YaronDL on 7/2/2017.
 */
public class ModelFeatureAggregationBucketsService extends FixedDurationStrategyExecutor {

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketStore featureBucketStore;

    public ModelFeatureAggregationBucketsService(BucketConfigurationService bucketConfigurationService,
                                                 EnrichedDataStore enrichedDataStore, InMemoryFeatureBucketAggregator featureBucketAggregator,
                                                 FeatureBucketStore featureBucketStore) {
        super(FixedDurationStrategy.DAILY);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
        this.featureBucketStore = featureBucketStore;
    }

    @Override
    protected void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType) {
        //For now we don't have multiple contexts so we pass just list of size 1.
        List<String> contextTypes = Collections.singletonList(contextType);

        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);
        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBucketsToInsert = featureBucketAggregator.aggregate(pageIterator,contextTypes, createFeatureBucketStrategyData(timeRange));
            storeFeatureBuckets(featureBucketsToInsert);
        }
    }

    private void storeFeatureBuckets(List<FeatureBucket> featureBucketList){
        Map<String,List<FeatureBucket>> confnameToFeatureBucketsMap = new HashMap<>();
        for(FeatureBucket featureBucket: featureBucketList){
            List<FeatureBucket> confFeatureBucketList = confnameToFeatureBucketsMap.get(featureBucket.getFeatureBucketConfName());
            if(confFeatureBucketList == null){
                confFeatureBucketList = new ArrayList<>();
                confnameToFeatureBucketsMap.put(featureBucket.getFeatureBucketConfName(), confFeatureBucketList);
            }
            confFeatureBucketList.add(featureBucket);
        }

        for(Map.Entry<String,List<FeatureBucket>> entry: confnameToFeatureBucketsMap.entrySet()){
            FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(entry.getKey());
            //todo add batch implementation to the feature bucket store
            for(FeatureBucket featureBucket: entry.getValue()){
                featureBucketStore.storeFeatureBucket(featureBucketConf, featureBucket);
            }
        }
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange){
        String strategyName = "fixed_duration_" + StringUtils.lowerCase(this.strategy.name());
        return new FeatureBucketStrategyData(strategyName,strategyName,timeRange.getStart().getEpochSecond(), timeRange.getEnd().getEpochSecond());
    }

    @Override
    protected List<String> getDistinctContextTypes(String adeEventType) {
        Set<List<String>> distinctMultipleContextsTypeSet = bucketConfigurationService.getRelatedDistinctContexts(adeEventType);
        Set<String> distinctSingleContextTypeSet = new HashSet<>();
        for(List<String> distinctMultipleContexts: distinctMultipleContextsTypeSet){
            distinctSingleContextTypeSet.addAll(distinctMultipleContexts);
        }
        return new ArrayList<>(distinctSingleContextTypeSet);
    }


}
