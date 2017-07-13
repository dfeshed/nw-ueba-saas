package presidio.ade.domain.store.aggr;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggrRecordsMetadata {
    public String featureName;

    public AggrRecordsMetadata(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }
}
