package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.Set;

@Configurable(preConstruction = true)
public class FeatureBucketContextSelector implements IContextSelector {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	private FeatureBucketConf featureBucketConf;

	public FeatureBucketContextSelector(FeatureBucketContextSelectorConf config) {
		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		validate(config);
	}

	@Override
	public Set<String> getContexts(Date startTime, Date endTime) {
		long startInSeconds = TimestampUtils.convertToSeconds(startTime);
		long endInSeconds = TimestampUtils.convertToSeconds(endTime);
		return featureBucketReader.getDistinctContextIds(featureBucketConf, new TimeRange(startInSeconds, endInSeconds));
	}

	private void validate(FeatureBucketContextSelectorConf config) {
		if (featureBucketConf == null)
			throw new InvalidFeatureBucketConfNameException(config.getFeatureBucketConfName());
	}
}
