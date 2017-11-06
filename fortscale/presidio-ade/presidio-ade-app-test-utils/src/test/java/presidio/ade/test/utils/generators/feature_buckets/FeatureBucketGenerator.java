package presidio.ade.test.utils.generators.feature_buckets;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketUtils;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.common.feature.Feature;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IMapGenerator;
import presidio.data.generators.common.RegexStringListGenerator;
import presidio.data.generators.common.time.ITimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A generator of {@link FeatureBucket}s.
 *
 * The generator traverses all the context IDs (that are derived from the {@link #featureBucketConf} and the
 * {@link #contextFieldValuesGenerator}), and for each one it resets the {@link #startTimeGenerator} and the
 * {@link #aggregatedFeaturesGenerator} in order to generate the same feature buckets for all the context IDs
 * (i.e. with the same start instants and aggregated features).
 *
 * This means that the {@link #startTimeGenerator} must be finite and not cyclic.
 *
 * @author Lior Govrin
 */
public class FeatureBucketGenerator {
    private final FeatureBucketConf featureBucketConf;
    private final ITimeGenerator startTimeGenerator;
    private final RegexStringListGenerator contextFieldValuesGenerator;
    private final IMapGenerator<String, Feature> aggregatedFeaturesGenerator;

    /**
     * C'tor.
     *
     * @param featureBucketConf           the configuration of the generated feature buckets
     * @param startTimeGenerator          the generator of the feature buckets' start instants
     * @param contextFieldValuesGenerator the generator of the feature buckets' context values
     * @param aggregatedFeaturesGenerator the generator of the feature buckets' aggregated features
     */
    public FeatureBucketGenerator(
            FeatureBucketConf featureBucketConf,
            ITimeGenerator startTimeGenerator,
            RegexStringListGenerator contextFieldValuesGenerator,
            IMapGenerator<String, Feature> aggregatedFeaturesGenerator) {

        int sizeOfGeneratedLists = contextFieldValuesGenerator.getSizeOfGeneratedLists();
        int contextFieldNamesSize = featureBucketConf.getContextFieldNames().size();
        Assert.isTrue(sizeOfGeneratedLists == contextFieldNamesSize, String.format(
                "The number of context fields in the values generator (%d) does not match the number of " +
                "context fields in the feature bucket conf (%d).", sizeOfGeneratedLists, contextFieldNamesSize
        ));
        this.featureBucketConf = featureBucketConf;
        this.startTimeGenerator = startTimeGenerator;
        this.contextFieldValuesGenerator = contextFieldValuesGenerator;
        this.aggregatedFeaturesGenerator = aggregatedFeaturesGenerator;
    }

    public List<FeatureBucket> generate() throws GeneratorException {
        String strategyName = featureBucketConf.getStrategyName();
        Duration duration = FixedDurationStrategy.fromStrategyName(strategyName).toDuration();
        String featureBucketConfName = featureBucketConf.getName();
        List<String> contextFieldNames = featureBucketConf.getContextFieldNames();
        List<FeatureBucket> featureBuckets = new LinkedList<>();

        while (contextFieldValuesGenerator.hasNext()) {
            List<String> contextFieldValues = contextFieldValuesGenerator.getNext();
            Map<String, String> contextFieldNameToValueMap = IntStream.range(0, contextFieldNames.size()).boxed()
                    .collect(Collectors.toMap(contextFieldNames::get, contextFieldValues::get));
            String contextId = FeatureBucketUtils.buildContextId(contextFieldNameToValueMap);
            startTimeGenerator.reset();
            aggregatedFeaturesGenerator.reset();

            while (startTimeGenerator.hasNext()) {
                Instant startTime = startTimeGenerator.getNext();
                Instant endTime = startTime.plus(duration);
                String strategyId = createStrategyId(strategyName, startTime, endTime);
                String bucketId = createBucketId(contextFieldNameToValueMap, strategyId);
                FeatureBucket featureBucket = new FeatureBucket();
                featureBucket.setStartTime(startTime);
                featureBucket.setEndTime(endTime);
                featureBucket.setFeatureBucketConfName(featureBucketConfName);
                featureBucket.setContextFieldNames(contextFieldNames);
                featureBucket.setStrategyId(strategyId);
                featureBucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
                featureBucket.setContextId(contextId);
                featureBucket.setBucketId(bucketId);
                featureBucket.setCreatedAt(new Date());
                featureBucket.setAggregatedFeatures(aggregatedFeaturesGenerator.getNext());
                featureBuckets.add(featureBucket);
            }
        }

        return featureBuckets;
    }

    private String createStrategyId(String strategyName, Instant startTime, Instant endTime) {
        TimeRange timeRange = new TimeRange(startTime, endTime);
        return new FeatureBucketStrategyData(strategyName, strategyName, timeRange).getStrategyId();
    }

    private String createBucketId(Map<String, String> contextFieldNameToValueMap, String strategyId) {
        AdeRecordReader adeRecordReader = new AdeRecordReader(null) {
            @Override
            public String getContext(String contextFieldName) {
                return contextFieldNameToValueMap.get(contextFieldName);
            }
        };
        return FeatureBucketUtils.buildBucketId(adeRecordReader, featureBucketConf, strategyId);
    }
}
