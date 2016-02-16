package fortscale.ml.model;

/**
 * Created by baraks on 2/11/2016.
 */
public class InvalidFeatureNameException extends RuntimeException {
    public InvalidFeatureNameException(String featureName, String featureBucketConfName) {
        super(String.format("FeatureName: %s does not exist in FeatureBucketConfName: %s  at buckets.json configuration", featureName, featureBucketConfName));
    }
}
