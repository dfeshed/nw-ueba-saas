package fortscale.accumulator.aggregation;

import fortscale.utils.data.Pair;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public class AccumulationsInMemory implements AccumulationsCache {

    //map<pair<contextId, featureName>,AccumulatedAggregationFeatureRecord>
    private Map<Pair<String, String>, AccumulatedAggregationFeatureRecord> memoryStore;

    public AccumulationsInMemory(){
        memoryStore = new HashMap<>();
    }

    @Override
    public void storeAccumulatedRecords(String featureName, String contextId, AccumulatedAggregationFeatureRecord accumulatedAggregationFeatureRecord) {
        Pair<String, String> contextIdToFeature = new Pair<>(contextId, featureName);
        memoryStore.put(contextIdToFeature, accumulatedAggregationFeatureRecord);
    }

    @Override
    public AccumulatedAggregationFeatureRecord getAccumulatedRecord(String featureName, String contextId) {
        Pair<String, String> contextIdToFeature = new Pair<>(contextId, featureName);
        return memoryStore.get(contextIdToFeature);
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> getAllAccumulatedRecords() {
        return memoryStore.values().stream().collect(Collectors.toList());
    }

    @Override
    public void clean(){
        memoryStore = new HashMap<>();
    }

}
