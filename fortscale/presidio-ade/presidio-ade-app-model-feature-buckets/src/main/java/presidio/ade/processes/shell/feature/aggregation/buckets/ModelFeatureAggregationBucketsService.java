package presidio.ade.processes.shell.feature.aggregation.buckets;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by YaronDL on 7/2/2017.
 */
public class ModelFeatureAggregationBucketsService extends FixedDurationStrategyExecutor {

    private BucketConfigurationService bucketConfigurationService;
    private EnrichedDataStore enrichedDataStore;
    private InMemoryFeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketStore featureBucketStore;
    private int pageSize;
    private int maxGroupSize;

    public ModelFeatureAggregationBucketsService(BucketConfigurationService bucketConfigurationService,
                                                 EnrichedDataStore enrichedDataStore, InMemoryFeatureBucketAggregator featureBucketAggregator,
                                                 FeatureBucketStore featureBucketStore, int pageSize, int maxGroupSize) {
        super(FixedDurationStrategy.DAILY);
        this.bucketConfigurationService = bucketConfigurationService;
        this.enrichedDataStore = enrichedDataStore;
        this.featureBucketAggregator = featureBucketAggregator;
        this.featureBucketStore = featureBucketStore;
        this.pageSize = pageSize;
        this.maxGroupSize = maxGroupSize;
    }

    @Override
    protected void executeSingleTimeRange(TimeRange timeRange, String adeEventType, String contextType, List<String> contextFieldNamesToExclude, StoreMetadataProperties storeMetadataProperties) {
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, pageSize, maxGroupSize, contextType);
        List<PageIterator<EnrichedRecord>> pageIterators = enrichedRecordPaginationService.getPageIterators(adeEventType, timeRange);
        FeatureBucketStrategyData featureBucketStrategyData = createFeatureBucketStrategyData(timeRange);

        for (PageIterator<EnrichedRecord> pageIterator : pageIterators) {
            List<FeatureBucket> featureBucketsToInsert =
                    featureBucketAggregator.aggregate(pageIterator, adeEventType, contextType, contextFieldNamesToExclude, featureBucketStrategyData);
            storeFeatureBuckets(featureBucketsToInsert, storeMetadataProperties);
        }
    }

    private void storeFeatureBuckets(List<FeatureBucket> featureBucketList, StoreMetadataProperties storeMetadataProperties){
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
            featureBucketStore.storeFeatureBucket(featureBucketConf, entry.getValue(), storeMetadataProperties);
        }
    }

    protected FeatureBucketStrategyData createFeatureBucketStrategyData(TimeRange timeRange) {
        String strategyName = strategy.toStrategyName();
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
    }

    @Override
    protected List<String> getDistinctContextTypes(String adeEventType, FixedDurationStrategy strategy) {
        return bucketConfigurationService.getMinimalContextList(adeEventType, strategy.toStrategyName());
    }

}
