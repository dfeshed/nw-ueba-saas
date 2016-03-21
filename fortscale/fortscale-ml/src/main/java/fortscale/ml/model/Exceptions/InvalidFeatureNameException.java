package fortscale.ml.model.exceptions;


public class InvalidFeatureNameException extends RuntimeException {
    public InvalidFeatureNameException(String featureName, String featureBucketConfName) {
        super(String.format("FeatureName: %s does not exist in FeatureBucketConfName: %s  at buckets.json configuration", featureName, featureBucketConfName));
    }
}
