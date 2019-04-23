package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordContributors.Contributor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScoreAggregationRecordSplitter {
    private final AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private final ScoredDataReaderViewerSwitch scoredDataReaderViewerSwitch;
    private final int scoreAggregationRecordContributorsLimit;

    public ScoreAggregationRecordSplitter(
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            RecordReaderFactoryService recordReaderFactoryService,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            ScoredDataReaderViewerSwitch scoredDataReaderViewerSwitch,
            int scoreAggregationRecordContributorsLimit) {

        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.scoredDataReaderViewerSwitch = scoredDataReaderViewerSwitch;
        this.scoreAggregationRecordContributorsLimit = scoreAggregationRecordContributorsLimit;
    }

    public ScoreAggregationRecordContributors split(AdeAggregationRecord scoreAggregationRecord, List<String> splitFieldNames) {
        ScoreAggregationRecordDetails scoreAggregationRecordDetails = new ScoreAggregationRecordDetails(
                scoreAggregationRecord, aggregatedFeatureEventsConfService, recordReaderFactoryService);
        Class<? extends AdeScoredRecord> scoredRecordClass = scoreAggregationRecordDetails.getScoredRecordClass();
        ScoredDataReaderViewer<? extends AdeRecord> scoredDataReaderViewer = scoredDataReaderViewerSwitch.get(scoredRecordClass);

        // (Re)build the feature bucket that the score aggregation record is based upon.
        FeatureBucket featureBucket = inMemoryFeatureBucketAggregator.createFeatureBucket(
                scoredDataReaderViewer.getScoredRecordPageIterator(scoreAggregationRecordDetails),
                scoreAggregationRecordDetails.getFeatureBucketConfName(),
                scoreAggregationRecordDetails.getFeatureBucketStrategyData());

        // Get the function that built the score aggregation record.
        Map<MultiKeyFeature, Double> map = scoreAggregationRecordDetails.getAggrFeatureEventFunction()
                // Calculate the contribution ratio of each tuple in the relevant aggregated feature (histogram).
                .calculateContributionRatios(scoreAggregationRecordDetails.getAggregatedFeatureEventConf(), featureBucket)
                // Iterate the tuples and their contribution ratios (<tuple, contribution ratio> entries).
                .getHistogram().entrySet().stream()
                // Reduce the <tuple, contribution ratio> entries according to the split field names.
                .collect(Collectors.toMap(
                        // Key mapper: Leave only the split field names and values.
                        entry -> reduceToSplitContext(splitFieldNames, entry.getKey()),
                        // Value mapper: Leave the contribution ratio as is.
                        Entry::getValue,
                        // Merge function: Sum all the contribution ratios that fall under the same reduced key.
                        Double::sum));

        // Iterate the entries of reduced tuples and contribution ratios.
        Stream<Entry<MultiKeyFeature, Double>> stream = map.entrySet().stream();
        if (scoreAggregationRecordContributorsLimit >= 0) stream = stream
                // Sort the entries according to their contribution ratios, in descending order.
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                // Limit the number of entries.
                .limit(scoreAggregationRecordContributorsLimit);

        List<Contributor> contributors = stream
                // Map each reduced tuple and contribution ratio to a Contributor instance.
                .map(entry -> createContributor(entry, scoreAggregationRecordDetails, scoredDataReaderViewer))
                // Collect all the Contributor instances.
                .collect(Collectors.toList());

        return new ScoreAggregationRecordContributors(scoredRecordClass, contributors);
    }

    private static MultiKeyFeature reduceToSplitContext(List<String> splitFieldNames, MultiKeyFeature context) {
        MultiKeyFeature splitContext = new MultiKeyFeature();
        splitFieldNames.forEach(splitFieldName -> {
            String splitFieldValue = context.getFeatureNameToValue().get(splitFieldName);
            splitContext.add(splitFieldName, splitFieldValue);
        });
        return splitContext;
    }

    private static Contributor createContributor(
            Entry<MultiKeyFeature, Double> entry,
            ScoreAggregationRecordDetails scoreAggregationRecordDetails,
            ScoredDataReaderViewer scoredDataReaderViewer) {

        // Extract the tuple and its contribution ratio.
        MultiKeyFeature tuple = entry.getKey();
        double contributionRatio = entry.getValue();
        // Get the first and the last underlying scored records with this tuple.
        TimeRange timeRange = scoreAggregationRecordDetails.getTimeRange();
        String scoredRecordAdeEventType = scoreAggregationRecordDetails.getScoredRecordAdeEventType();
        MultiKeyFeature context = scoreAggregationRecordDetails.getContextFieldNameToValueMap();
        AdeScoredRecord firstScoredRecord = scoredDataReaderViewer.getFirstScoredRecord(
                timeRange, scoredRecordAdeEventType, context, tuple);
        AdeScoredRecord lastScoredRecord = scoredDataReaderViewer.getLastScoredRecord(
                timeRange, scoredRecordAdeEventType, context, tuple);
        // Create a new Contributor instance.
        return new Contributor(tuple, contributionRatio, firstScoredRecord, lastScoredRecord);
    }
}
