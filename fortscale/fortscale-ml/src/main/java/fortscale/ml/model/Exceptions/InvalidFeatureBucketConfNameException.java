package fortscale.ml.model.Exceptions;


public class InvalidFeatureBucketConfNameException extends RuntimeException{
    public InvalidFeatureBucketConfNameException(String featureBucketConfName) {
        super(String.format("FeatureBucketConfName: %s configuration does not exist in buckets.json configuration", featureBucketConfName));
    }
}
