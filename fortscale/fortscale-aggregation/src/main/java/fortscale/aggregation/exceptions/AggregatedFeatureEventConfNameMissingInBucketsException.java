package fortscale.aggregation.exceptions;


public class AggregatedFeatureEventConfNameMissingInBucketsException extends RuntimeException{
    public AggregatedFeatureEventConfNameMissingInBucketsException(String BucketConfName) {
        super(String.format("BucketConfName: %s from aggregated_feature_events.json configuration does not exist in buckets.json configuration", BucketConfName));
    }
}
