package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;

public class JokerAggrEventData {
    String featureType;
    String aggregatedFeatureName;
    Double aggregatedFeatureValue;
    String bucketConfName;
    Double score;

    public JokerAggrEventData(AggrEvent aggrEvent) {
        this.featureType = aggrEvent.getFeatureType();
        this.aggregatedFeatureName = aggrEvent.getAggregatedFeatureName();
        this.aggregatedFeatureValue = aggrEvent.getAggregatedFeatureValue();
        this.bucketConfName = aggrEvent.getBucketConfName();
        this.score = aggrEvent.getScore();
    }

    public String getFeatureType() {
        return featureType;
    }

    public String getAggregatedFeatureName() {
        return aggregatedFeatureName;
    }

    public Double getAggregatedFeatureValue() {
        return aggregatedFeatureValue;
    }

    public String getBucketConfName() {
        return bucketConfName;
    }

    public Double getScore() {
        return score;
    }

    public boolean isOfTypeF() {
        return AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE.equals(getFeatureType());
    }

    public boolean isOfTypeP() {
        return AggrEvent.AGGREGATED_FEATURE_TYPE_P_VALUE.equals(getFeatureType());
    }

}
