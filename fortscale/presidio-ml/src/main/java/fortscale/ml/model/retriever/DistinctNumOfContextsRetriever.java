package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.*;

public class DistinctNumOfContextsRetriever extends AbstractDataRetriever {

    private final FeatureBucketReader featureBucketReader;
    private final FeatureBucketConf featureBucketConf;

    public DistinctNumOfContextsRetriever(DistinctNumOfContextsRetrieverConf config, BucketConfigurationService bucketConfigurationService,
                                          FeatureBucketReader featureBucketReader) {
        super(config);
        this.featureBucketReader = featureBucketReader;
        String featureBucketConfName = config.getFeatureBucketConfName();
        this.featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
        long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
        long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
        long countOfDistinctContextIds = featureBucketReader.getNumOfDistinctContextIds(featureBucketConf, new TimeRange(startTimeInSeconds, endTimeInSeconds));
        if (countOfDistinctContextIds == 0L) return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
        return new ModelBuilderData(countOfDistinctContextIds);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public Set<String> getEventFeatureNames() {
        throw new UnsupportedOperationException(String.format("%s does not support getEventFeatureNames",
                getClass().getSimpleName()));
    }

    @Override
    public List<String> getContextFieldNames() {
        return Collections.emptyList();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        return null;
    }

}
