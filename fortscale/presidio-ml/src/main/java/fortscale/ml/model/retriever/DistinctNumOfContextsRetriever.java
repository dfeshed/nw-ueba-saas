package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.ml.model.exceptions.InvalidFeatureNameException;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DistinctNumOfContextsRetriever extends AbstractDataRetriever {

    private final BucketConfigurationService bucketConfigurationService;
    private final FeatureBucketReader featureBucketReader;
    private final FeatureBucketConf featureBucketConf;
    private final String featureName;


    public DistinctNumOfContextsRetriever(DistinctNumOfContextsRetrieverConf config, BucketConfigurationService bucketConfigurationService,
                                          FeatureBucketReader featureBucketReader) {
        super(config);
        this.bucketConfigurationService = bucketConfigurationService;
        this.featureBucketReader = featureBucketReader;
        String featureBucketConfName = config.getFeatureBucketConfName();
        this.featureBucketConf = this.bucketConfigurationService.getBucketConf(featureBucketConfName);
        this.featureName = config.getFeatureName();
        validate(config);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId, "context must be null");
        Set<String> contextIds = getDistinctContextIds(endTime);
        if (contextIds.isEmpty()) return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
        return new ModelBuilderData(contextIds.size());
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public Set<String> getEventFeatureNames() {
        return featureBucketConf.getAggregatedFeatureConf(featureName).getAllFeatureNames();
    }

    @Override
    public List<String> getContextFieldNames() {
        return featureBucketConf.getContextFieldNames();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        Assert.notEmpty(context, "context cannot be empty.");
        return FeatureBucketUtils.buildContextId(context);
    }

    private Set<String> getDistinctContextIds(Date endTime) {
        long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
        long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
        return featureBucketReader.getDistinctContextIds(featureBucketConf, new TimeRange(startTimeInSeconds, endTimeInSeconds));
    }


    private void validate(DistinctNumOfContextsRetrieverConf config) {
        if (featureBucketConf == null) {
            throw new InvalidFeatureBucketConfNameException(config.getFeatureBucketConfName());
        }

        for (AggregatedFeatureConf aggrFeatureConf : featureBucketConf.getAggrFeatureConfs()) {
            if (aggrFeatureConf.getName().equals(featureName)) return;
        }

        throw new InvalidFeatureNameException(featureName, config.getFeatureBucketConfName());
    }
}
