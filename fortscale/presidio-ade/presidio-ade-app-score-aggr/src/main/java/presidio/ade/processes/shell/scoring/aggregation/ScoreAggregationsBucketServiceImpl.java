package presidio.ade.processes.shell.scoring.aggregation;

import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 6/14/17.
 */
public class ScoreAggregationsBucketServiceImpl implements ScoreAggregationsBucketService{
    private FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private BucketConfigurationService bucketConfigurationService;
    private RecordReaderFactoryService recordReaderFactoryService;
    private FeatureBucketAggregator featureBucketAggregator;
    private FeatureBucketsAggregatorInMemory featureBucketsInMemory;

    public ScoreAggregationsBucketServiceImpl(BucketConfigurationService bucketConfigurationService, RecordReaderFactoryService recordReaderFactoryService, FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer) {
        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        initFeatureBucketAggregator();
    }

    private void initFeatureBucketAggregator(){
        this.featureBucketsInMemory = new FeatureBucketsAggregatorInMemory();
        this.featureBucketAggregator = new FeatureBucketAggregator(featureBucketsInMemory,recordReaderFactoryService, featureBucketAggregatorMetricsContainer);
    }

    @Override
    public void updateBuckets(List<AdeScoredEnrichedRecord> adeRecordList, String contextFieldName,
                              List<String> excludeContextFieldNames, FeatureBucketStrategyData strategyData) {
        if(adeRecordList.isEmpty()){
            return;
        }
        Map<String, List<FeatureBucketConf>> adeEventTypeToFeatureBucketConfList = new HashMap<>();
        for(AdeScoredEnrichedRecord record: adeRecordList){
            String adeEventType = record.getAdeEventType();
            List<FeatureBucketConf> featureBucketConfs = adeEventTypeToFeatureBucketConfList.get(adeEventType);
            if(featureBucketConfs == null){
                featureBucketConfs =
                        bucketConfigurationService.getRelatedBucketConfs(adeEventType, strategyData.getStrategyName(),
                                contextFieldName, excludeContextFieldNames);
                adeEventTypeToFeatureBucketConfList.put(adeEventType, featureBucketConfs);
            }
            featureBucketAggregator.aggregate(record, featureBucketConfs, strategyData);
        }
    }

    @Override
    public List<FeatureBucket> closeBuckets() {
        List<FeatureBucket> ret = featureBucketsInMemory.getAllFeatureBuckets();
        initFeatureBucketAggregator();

        return ret;
    }
}
