package fortscale.accumulator.smart;

import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;

import java.util.List;


public interface SmartAccumulationsCache {

    /**
     * Store accumulated record in memory
     *
     * @param contextId              context id
     * @param accumulatedSmartRecord accumulated smart record
     */
    void storeAccumulatedRecords(String contextId, AccumulatedSmartRecord accumulatedSmartRecord);

    /**
     * Get accumulated record by featureName and contextId
     *
     * @param contextId context id
     * @return AccumulatedAggregationFeatureRecord
     */
    AccumulatedSmartRecord getAccumulatedRecord(String contextId);

    /**
     * Get all accumulated records
     *
     * @return List<AccumulatedAggregationFeatureRecord>
     */
    List<AccumulatedSmartRecord> getAllAccumulatedRecords();

    /**
     * Clean the memory
     */
    void clean();
}
