package presidio.ade.domain.pagination.aggregated;

import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

public class AggregatedDataPaginationParam {

    private final String featureName;
    private final AggregatedFeatureType aggregatedFeatureType;

    public AggregatedDataPaginationParam(String featureName, AggregatedFeatureType aggregatedFeatureType) {
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
