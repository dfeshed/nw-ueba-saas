package fortscale.accumulator.smart;

import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public class SmartAccumulationsInMemory implements SmartAccumulationsCache {

    //map<contextId, AccumulatedAggregationFeatureRecord>
    private Map<String, AccumulatedSmartRecord> memoryStore;

    public SmartAccumulationsInMemory() {
        memoryStore = new HashMap<>();
    }

    @Override
    public void storeAccumulatedRecords(String contextId, AccumulatedSmartRecord accumulatedSmartRecord) {
        memoryStore.put(contextId, accumulatedSmartRecord);
    }

    @Override
    public AccumulatedSmartRecord getAccumulatedRecord(String contextId) {
        return memoryStore.get(contextId);
    }

    @Override
    public List<AccumulatedSmartRecord> getAllAccumulatedRecords() {
        return memoryStore.values().stream().collect(Collectors.toList());
    }

    @Override
    public void clean() {
        memoryStore = new HashMap<>();
    }

}
