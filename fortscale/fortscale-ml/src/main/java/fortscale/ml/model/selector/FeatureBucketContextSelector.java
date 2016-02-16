package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.ml.model.Exceptions.InvalidFeatureBucketConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class FeatureBucketContextSelector implements IContextSelector {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;
	private String featureBucketConfName;

	public FeatureBucketContextSelector(FeatureBucketContextSelectorConf config) {
		featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		validate();
	}
	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return featureBucketsReaderService.findDistinctContextByTimeRange(
				featureBucketConf, startTime.getTime(), endTime.getTime());
	}
	private void validate()
	{
		if (featureBucketConf==null)
			throw new InvalidFeatureBucketConfNameException(featureBucketConfName);
	}
}
