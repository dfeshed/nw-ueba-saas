package fortscale.ml.model;

/**
 * Created by baraks on 2/11/2016.
 */
public class InvalidAggregatedFeatureEventConfNameException extends RuntimeException{
    public InvalidAggregatedFeatureEventConfNameException(String aggregatedFeatureEventConfName) {
        super(String.format("AggregatedFeatureEventConfName: %s configuration does not exist in aggregated_feature_events.json configuration", aggregatedFeatureEventConfName));
    }
}
