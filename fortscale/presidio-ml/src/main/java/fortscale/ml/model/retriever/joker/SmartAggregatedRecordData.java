package fortscale.ml.model.retriever.joker;

/**
 * Created by barak_schuster on 27/08/2017.
 */
public class SmartAggregatedRecordData {
    private final String featureName;
    private final Double score;

    public SmartAggregatedRecordData(String featureName, Double score) {
        this.featureName = featureName;
        this.score = score;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getScore() {
        return score;
    }
}
