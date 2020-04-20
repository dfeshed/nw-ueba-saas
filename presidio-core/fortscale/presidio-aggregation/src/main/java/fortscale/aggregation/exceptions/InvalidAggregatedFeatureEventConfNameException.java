package fortscale.aggregation.exceptions;


public class InvalidAggregatedFeatureEventConfNameException extends RuntimeException{
    public InvalidAggregatedFeatureEventConfNameException(String aggregatedFeatureEventConfName) {
        super(String.format("AggregatedFeatureEventConfName: %s configuration does not exist in aggregated_feature_events.json configuration", aggregatedFeatureEventConfName));
    }
}
