package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.ml.model.AggregatedFeatureValuesData;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

import static fortscale.aggregation.feature.functions.AggrFeatureFeatureToMaxMapFunc.FEATURE_GROUP_SEPARATOR_KEY;
import static fortscale.aggregation.feature.functions.AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY;

/**
 * @author Lior Govrin
 */
public class EpochtimeToHighestDoubleMapRetriever extends AbstractDataRetriever {
    private final FeatureBucketReader featureBucketReader;
    private final FeatureBucketConf featureBucketConf;
    private final String featureName;
    private final long epochtimeResolutionInSeconds;
    private final Duration epochtimeResolution;

    public EpochtimeToHighestDoubleMapRetriever(
            FeatureBucketReader featureBucketReader,
            BucketConfigurationService bucketConfigurationService,
            EpochtimeToHighestDoubleMapRetrieverConf conf) {

        super(conf);
        this.featureBucketReader = featureBucketReader;
        this.featureBucketConf = bucketConfigurationService.getBucketConf(conf.getFeatureBucketConfName());
        this.featureName = conf.getFeatureName();
        this.epochtimeResolutionInSeconds = conf.getEpochtimeResolutionInSeconds();
        this.epochtimeResolution = Duration.ofSeconds(epochtimeResolutionInSeconds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        List<FeatureBucket> featureBuckets = featureBucketReader.getFeatureBuckets(
                featureBucketConf.getName(), contextId, new TimeRange(getStartTime(endTime), endTime));
        if (featureBuckets.isEmpty()) return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
        TreeMap<Instant, Double> instantToHighestDoubleMap = new TreeMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {
            Feature aggregatedFeature = featureBucket.getAggregatedFeatures().get(featureName);

            if (aggregatedFeature != null) {
                AggrFeatureValue aggrFeatureValue = (AggrFeatureValue)aggregatedFeature.getValue();
                Map<String, Double> epochtimeToHighestDoubleMap = (Map<String, Double>)aggrFeatureValue.getValue();
                Date dataTime = Date.from(featureBucket.getStartTime());

                for (IDataRetrieverFunction function : functions) {
                    epochtimeToHighestDoubleMap = (Map<String, Double>)function.execute(
                            epochtimeToHighestDoubleMap, dataTime, endTime);
                }

                updateInstantToHighestDoubleMap(epochtimeToHighestDoubleMap, instantToHighestDoubleMap);
            }
        }

        if (instantToHighestDoubleMap.isEmpty()) return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
        return new ModelBuilderData(new AggregatedFeatureValuesData(epochtimeResolution, instantToHighestDoubleMap));
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        String s = String.format("%s does not support the retrieval of a single feature.", getClass().getSimpleName());
        throw new UnsupportedOperationException(s);
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

    private void updateInstantToHighestDoubleMap(
            Map<String, Double> epochtimeToHighestDoubleMap,
            TreeMap<Instant, Double> instantToHighestDoubleMap) {

        for (Entry<String, Double> entry : epochtimeToHighestDoubleMap.entrySet()) {
            long epochtime = convertKeyToEpochtime(entry.getKey());
            epochtime = (epochtime / epochtimeResolutionInSeconds) * epochtimeResolutionInSeconds;
            Instant instant = Instant.ofEpochSecond(epochtime);
            Double currentHighestDouble = instantToHighestDoubleMap.get(instant);
            Double potentiallyHighestDouble = entry.getValue();

            if (currentHighestDouble == null) {
                instantToHighestDoubleMap.put(instant, potentiallyHighestDouble);
            } else {
                instantToHighestDoubleMap.put(instant, Math.max(currentHighestDouble, potentiallyHighestDouble));
            }
        }
    }

    private long convertKeyToEpochtime(String key) {
        if (key.split(FEATURE_GROUP_SEPARATOR_KEY).length != 1) {
            String s = String.format("%s supports only keys containing 1 feature.", getClass().getSimpleName());
            throw new IllegalArgumentException(s);
        }

        String[] featureNameAndValue = key.split(FEATURE_SEPARATOR_KEY);

        if (featureNameAndValue.length != 2) {
            String s = String.format("Invalid key: %s.", key);
            throw new IllegalArgumentException(s);
        }

        return Long.parseLong(featureNameAndValue[1]);
    }
}
