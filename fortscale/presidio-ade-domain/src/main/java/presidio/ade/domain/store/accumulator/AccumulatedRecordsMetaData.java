package presidio.ade.domain.store.accumulator;

public class AccumulatedRecordsMetaData {
    private String featureName;

    public AccumulatedRecordsMetaData(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }
}
