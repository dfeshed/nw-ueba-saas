package fortscale.aggregation.feature.bucket;

/**
 * These are all the APIs for a {@link FeatureBucket} store. This interface configures the write only APIs,
 * and it extends {@link FeatureBucketReader}, where the read only APIs are configured.
 *
 * @author Lior Govrin
 */
public interface FeatureBucketStore extends FeatureBucketReader {
	/**
	 * Store the given {@link FeatureBucket} created from the given {@link FeatureBucketConf}.
	 *
	 * @param featureBucketConf the {@link FeatureBucketConf} from which the {@link FeatureBucket} was created
	 * @param featureBucket     the {@link FeatureBucket} to store
	 */
	void storeFeatureBucket(FeatureBucketConf featureBucketConf, FeatureBucket featureBucket);
}
