package presidio.ade.domain.store.aggr;

import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggrRecordsMetadata {
    private String featureName;
    private AggregatedFeatureType aggregatedFeatureType;

    public AggrRecordsMetadata(String featureName, AggregatedFeatureType aggregatedFeatureType) {
        this.featureName = featureName;
        this.aggregatedFeatureType = aggregatedFeatureType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public AggregatedFeatureType getAggregatedFeatureType() {
        return aggregatedFeatureType;
    }
}
