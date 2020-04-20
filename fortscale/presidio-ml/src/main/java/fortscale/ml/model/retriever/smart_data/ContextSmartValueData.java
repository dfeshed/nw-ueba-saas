package fortscale.ml.model.retriever.smart_data;


import java.time.Instant;
import java.util.Map;


public class ContextSmartValueData {
    private Map<Instant, Double> startInstantToSmartValue;
    private Instant weightsModelEndTime;

    public ContextSmartValueData(Map<Instant, Double> startInstantToSmartValue, Instant weightsModelEndTime) {
        this.startInstantToSmartValue = startInstantToSmartValue;
        this.weightsModelEndTime = weightsModelEndTime;
    }

    public Map<Instant, Double> getStartInstantToSmartValue() {
        return startInstantToSmartValue;
    }

    public Instant getWeightsModelEndTime() {
        return weightsModelEndTime;
    }
}
