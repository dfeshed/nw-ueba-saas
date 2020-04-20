package fortscale.accumulator.aggregation;

import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;

import java.util.List;


public interface AccumulationsCache {

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
