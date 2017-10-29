package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.ml.model.exceptions.InvalidFeatureNameException;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoricalFeatureValueRetriever extends AbstractDataRetriever {

    private final BucketConfigurationService bucketConfigurationService;
    private final FeatureBucketReader featureBucketReader;
    private final FeatureBucketConf featureBucketConf;
    private final String featureName;

    public CategoricalFeatureValueRetriever(CategoricalFeatureValueRetrieverConf config, BucketConfigurationService bucketConfigurationService, FeatureBucketReader featureBucketReader) {
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
        return doRetrieve(contextId, endTime, null);
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        return doRetrieve(contextId, endTime, feature.getValue().toString());
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

    protected ModelBuilderData doRetrieve(String contextId, Date endTime, String featureValue) {
        List<FeatureBucket> featureBuckets = getFeatureBuckets(contextId, endTime);

        if (featureBuckets.isEmpty()) return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);

        FixedDurationStrategy strategy = FixedDurationStrategy.fromStrategyName(featureBucketConf.getStrategyName());
        CategoricalFeatureValue reductionHistogram = new CategoricalFeatureValue(strategy);
        createReductionHistogram(endTime, featureValue, featureBuckets, reductionHistogram);

        return getModelBuilderData(reductionHistogram);
    }

    ModelBuilderData getModelBuilderData(CategoricalFeatureValue reductionHistogram) {
        if (reductionHistogram.getN() == 0) {
            return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
        } else {
            return new ModelBuilderData(reductionHistogram);
        }
    }

    void createReductionHistogram(Date endTime, String featureValue, List<FeatureBucket> featureBuckets, CategoricalFeatureValue reductionHistogram) {
        for (FeatureBucket featureBucket : featureBuckets) {
            Date dataTime = Date.from(featureBucket.getStartTime());
            Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();

            if (aggregatedFeatures.containsKey(featureName)) {

                GenericHistogram histogram = (GenericHistogram)aggregatedFeatures.get(featureName).getValue();
                if (featureValue != null) histogram = doFilter(histogram, featureValue);

                for (IDataRetrieverFunction function : functions) {
                    histogram = (GenericHistogram)function.execute(histogram, dataTime, endTime);
                }

                reductionHistogram.add(histogram,featureBucket.getStartTime());
            }
        }
    }

    protected List<FeatureBucket> getFeatureBuckets(String contextId, Date endTime) {
        long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
        long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

        return featureBucketReader.getFeatureBuckets(
                featureBucketConf.getName(), contextId,
                new TimeRange(startTimeInSeconds, endTimeInSeconds));
    }


    protected GenericHistogram doFilter(GenericHistogram original, String featureValue) {
        Double value = original.get(featureValue);
        GenericHistogram filtered = new GenericHistogram();
        if (value != null) filtered.add(featureValue, value);
        return filtered;
    }

    private void validate(CategoricalFeatureValueRetrieverConf config) {
        if (featureBucketConf == null) {
            throw new InvalidFeatureBucketConfNameException(config.getFeatureBucketConfName());
        }

        for (AggregatedFeatureConf aggrFeatureConf : featureBucketConf.getAggrFeatureConfs()) {
            if (aggrFeatureConf.getName().equals(featureName)) return;
        }

        throw new InvalidFeatureNameException(featureName, config.getFeatureBucketConfName());
    }
}
