package fortscale.ml.model.retriever.smart_data;

/**
 * @author Barak Schuster
 * @author Lior Govrin
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmartAggregatedRecordData)) return false;
        SmartAggregatedRecordData that = (SmartAggregatedRecordData)o;
        if (featureName != null ? !featureName.equals(that.featureName) : that.featureName != null) return false;
        return score != null ? score.equals(that.score) : that.score == null;
    }

    @Override
    public int hashCode() {
        int result = featureName != null ? featureName.hashCode() : 0;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}
