package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordContributors.Contributor;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ScoreAggregationRecordSplitter {
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private final ScoredRecordPageIteratorFactoryService scoredRecordPageIteratorFactoryService;

    public ScoreAggregationRecordSplitter(
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            ScoredRecordPageIteratorFactoryService scoredRecordPageIteratorFactoryService) {

        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.scoredRecordPageIteratorFactoryService = scoredRecordPageIteratorFactoryService;
    }

    public ScoreAggregationRecordContributors split(AdeAggregationRecord scoreAggregationRecord, List<String> splitFieldNames) {
        ScoreAggregationRecordDetails scoreAggregationRecordDetails = new ScoreAggregationRecordDetails(
                scoreAggregationRecord, aggregatedFeatureEventsConfService, recordReaderFactoryService);

        // (Re)build the feature bucket that the score aggregation record is based upon.
        FeatureBucket featureBucket = getFeatureBucket(scoreAggregationRecordDetails);
        // Get the function that built the score aggregation record.
        List<Contributor> contributors = scoreAggregationRecordDetails.getAggrFeatureEventFunction()
                // Calculate the contribution ratio of each tuple in the relevant aggregated feature (histogram).
                .calculateContributionRatios(scoreAggregationRecordDetails.getAggregatedFeatureEventConf(), featureBucket)
                // Iterate the tuples and their contribution ratios (<tuple, contribution ratio> entries).
                .getHistogram().entrySet().stream()
                // Reduce the <tuple, contribution ratio> entries according to the split field names.
                .collect(Collectors.toMap(
                        // Key mapper: Leave only the split field names and values.
                        entry -> reduceToSplitContexts(splitFieldNames, entry.getKey()),
                        // Value mapper: Leave the contribution ratio as is.
                        Entry::getValue,
                        // Merge function: Sum all the contribution ratios that fall under the same reduced key.
                        Double::sum))
                // Iterate the entries of reduced tuples and contribution ratios.
                .entrySet().stream()
                // Map each reduced tuple and contribution ratio to a Contributor instance.
                // TODO: Add the TimeRange of the Contributor.
                .map(entry -> new Contributor(entry.getKey(), entry.getValue(), null))
                // Collect all the Contributor instances.
                .collect(Collectors.toList());

        return new ScoreAggregationRecordContributors(scoreAggregationRecordDetails.getScoredRecordClass(), contributors);
    }

    private FeatureBucket getFeatureBucket(ScoreAggregationRecordDetails scoreAggregationRecordDetails) {
        return inMemoryFeatureBucketAggregator.createFeatureBucket(
                scoredRecordPageIteratorFactoryService.getScoredRecordPageIterator(scoreAggregationRecordDetails),
                scoreAggregationRecordDetails.getFeatureBucketConfName(),
                scoreAggregationRecordDetails.getFeatureBucketStrategyData());
    }

    private static MultiKeyFeature reduceToSplitContexts(
            List<String> splitFieldNames, MultiKeyFeature contextFieldNameToValueMap) {

        MultiKeyFeature splitFieldNameToValueMap = new MultiKeyFeature();
        splitFieldNames.forEach(splitFieldName -> {
            String splitFieldValue = contextFieldNameToValueMap.getFeatureNameToValue().get(splitFieldName);
            splitFieldNameToValueMap.add(splitFieldName, splitFieldValue);
        });
        return splitFieldNameToValueMap;
    }
}
