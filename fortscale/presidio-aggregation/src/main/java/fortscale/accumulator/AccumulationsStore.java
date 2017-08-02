package fortscale.accumulator;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;

import java.util.List;
import java.util.Map;


public interface AccumulationsStore {

    /**
     * Store accumulated record in memory
     * @param featureName feature name
     * @param contextId context id
     * @param accumulatedAggregationFeatureRecord accumulated record
     */
    void storeAccumulatedRecords(String featureName, String contextId, AccumulatedAggregationFeatureRecord accumulatedAggregationFeatureRecord);

    /**
     * Get accumulated record by featureName and contextId
     * @param featureName feature name
     * @param contextId context id
     * @return AccumulatedAggregationFeatureRecord
     */
    AccumulatedAggregationFeatureRecord getAccumulatedRecord(String featureName, String contextId);

    /**
     * Get all accumulated records
     * @return List<AccumulatedAggregationFeatureRecord>
     */
    List<AccumulatedAggregationFeatureRecord>  getAllAccumulatedRecords();

    /**
     * Clean the memory
     */
    void clean();
}
