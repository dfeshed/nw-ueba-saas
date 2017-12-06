package presidio.ade.test.utils.generators;

import fortscale.accumulator.smart.SmartAccumulationsCache;
import fortscale.accumulator.smart.SmartAccumulationsInMemory;
import fortscale.accumulator.smart.SmartAccumulatorService;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.TimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AccumulatedSmartsDailyGenerator {
    private static final int SEED = 1;

    private IStringGenerator contextIdGenerator;
    private TimeGenerator timeGenerator;
    private Map<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresToScoreAndProbabilityMap;
    private Random random;
    private int numOfSmartsPerDay;

    public AccumulatedSmartsDailyGenerator(IStringGenerator contextIdGenerator,
                                           TimeGenerator timeGenerator,
                                           Map<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresToScoreAndProbabilityMap,
                                           int numOfSmartsPerDay) throws GeneratorException {
        this.contextIdGenerator = contextIdGenerator;
        this.timeGenerator = timeGenerator;
        this.featuresToScoreAndProbabilityMap = featuresToScoreAndProbabilityMap;
        this.numOfSmartsPerDay = numOfSmartsPerDay;
        random = new Random(SEED);
    }

    /**
     * Generates accumulated smarts
     */
    public List<AccumulatedSmartRecord> generate() throws GeneratorException {

        List<AccumulatedSmartRecord> evList = new ArrayList<>();

        while (timeGenerator.hasNext()) {
            Instant startInstant = timeGenerator.getNext();
            Instant endInstant = startInstant.plus(FixedDurationStrategy.DAILY.toDuration());
            TimeRange accumulatedTimeRange = new TimeRange(startInstant, endInstant);

            List<SmartRecord> smarts = new ArrayList<>();

            while (smarts.size() < numOfSmartsPerDay) {
                String contextId = contextIdGenerator.getNext();

                for (int hour = 0; hour < 24 && smarts.size() < numOfSmartsPerDay; hour++) {
                    Instant smartStartInstant = startInstant.plus(Duration.ofHours(hour));
                    Instant smartEndInstant = smartStartInstant.plus(Duration.ofHours(1));
                    TimeRange smartTimeRange = new TimeRange(smartStartInstant, smartEndInstant);
                    Map<String, String> context = new HashMap<>();
                    context.put("userId", contextId);

                    SmartRecord smartRecord = new SmartRecord(smartTimeRange, contextId, "userId_hourly", FixedDurationStrategy.HOURLY, 0.0, 0.0, Collections.emptyList(), Collections.emptyList(), context, null, null);
                    List<AdeAggregationRecord> aggregationRecords = new ArrayList<>();
                    for (Map.Entry<List<AggregatedFeatureEventConf>, Pair<Double, Integer>> featuresToScoreAndProbability : featuresToScoreAndProbabilityMap.entrySet()) {
                        for (AggregatedFeatureEventConf feature : featuresToScoreAndProbability.getKey()) {
                            int probability = random.nextInt(100);
                            if (probability < featuresToScoreAndProbability.getValue().getValue()) {

                                if (feature.getType().equals("P")) {
                                    AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(smartStartInstant, smartEndInstant, feature.getName(), featuresToScoreAndProbability.getValue().getKey(), "", context, AggregatedFeatureType.SCORE_AGGREGATION);
                                    aggregationRecords.add(adeAggregationRecord);
                                } else {
                                    AdeAggregationRecord adeAggregationRecord = new ScoredFeatureAggregationRecord(
                                            featuresToScoreAndProbability.getValue().getKey(), Collections.emptyList(),
                                            smartStartInstant, smartEndInstant, feature.getName(), 0.0,
                                            "", context, AggregatedFeatureType.FEATURE_AGGREGATION);
                                    aggregationRecords.add(adeAggregationRecord);
                                }
                            }
                        }
                    }
                    smartRecord.setAggregationRecords(aggregationRecords);
                    smarts.add(smartRecord);
                }
            }

            SmartAccumulationsCache smartAccumulationsCache = new SmartAccumulationsInMemory();
            new SmartAccumulatorService(smartAccumulationsCache, accumulatedTimeRange).accumulate(smarts);
            evList.addAll(smartAccumulationsCache.getAllAccumulatedRecords());
        }

        return evList;
    }

}