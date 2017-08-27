package fortscale.ml.model.retriever.joker;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 27/08/2017.
 */
public class SmartAggregatedRecordDataContainer {
    private Instant startTime;
    private List<SmartAggregatedRecordData> jokerAggregatedRecordsData;

    public SmartAggregatedRecordDataContainer(Instant startTime, Map<String, Double> featureNameToScore) {
        this.startTime = startTime;
        this.jokerAggregatedRecordsData = featureNameToScore.entrySet().stream().map(entry -> new SmartAggregatedRecordData(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    public Instant getStartTime() {
        return startTime;
    }

    public List<SmartAggregatedRecordData> getJokerAggregatedRecordsData() {
        return jokerAggregatedRecordsData;
    }
}
