package fortscale.ml.model;

/**
 * Created by baraks on 2/11/2016.
 */
public class InvalidFeatureBucketConfNameException extends RuntimeException{
    public InvalidFeatureBucketConfNameException(String featureBucketConfName) {
        super(String.format("FeatureBucketConfName: %s configuration does not exist in buckets.json configuration", featureBucketConfName));
    }
}
