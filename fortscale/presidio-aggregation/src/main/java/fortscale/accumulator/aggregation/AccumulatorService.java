package fortscale.accumulator.aggregation;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public class AccumulatorService implements Accumulator {

    private final FixedDurationStrategy aggregationStrategy;
    private AccumulationsCache accumulationsInMemory;
    private FixedDurationStrategy accumulationStrategy;
    private long aggregationFixedDurationStrategyInMillis;

    public AccumulatorService(AccumulationsCache accumulationsInMemory, FixedDurationStrategy accumulationStrategy, FixedDurationStrategy aggregationStrategy) {
        this.accumulationsInMemory = accumulationsInMemory;
        this.accumulationStrategy = accumulationStrategy;
        this.aggregationStrategy = aggregationStrategy;
        this.aggregationFixedDurationStrategyInMillis = aggregationStrategy.toDuration().toMillis();
    }

    @Override
    public void accumulate(List<AdeAggregationRecord> adeAggregationRecords) {

        for (AdeAggregationRecord adeAggregationRecord : adeAggregationRecords) {
            String context = AdeContextualAggregatedRecord.getAggregatedFeatureContextId(adeAggregationRecord.getContext());
            String featureName = adeAggregationRecord.getFeatureName();

            AccumulatedAggregationFeatureRecord accumulatedRecord = accumulationsInMemory.getAccumulatedRecord(featureName, context);

            Instant adeAggregationRecordStartInstant = adeAggregationRecord.getStartInstant();
            if (accumulatedRecord == null) {
                Instant startInstant = TimeService.floorTime(adeAggregationRecordStartInstant, accumulationStrategy.toDuration());
                Instant endInstant = getEndInstant(startInstant);
                accumulatedRecord = new AccumulatedAggregationFeatureRecord(startInstant, endInstant, context, featureName);
            }
            int currentTimePartitionNumberInt = calcFeatureValueMapKey(accumulatedRecord, adeAggregationRecordStartInstant);
            accumulatedRecord.getAggregatedFeatureValues().put(currentTimePartitionNumberInt,adeAggregationRecord.getFeatureValue());
            accumulationsInMemory.storeAccumulatedRecords(featureName, context, accumulatedRecord);
        }

    }

    private int calcFeatureValueMapKey(AccumulatedAggregationFeatureRecord accumulatedRecord, Instant adeAggregationRecordStartInstant) {
        Instant accumulatedRecordStartInstant = accumulatedRecord.getStartInstant();
        Duration aggrPreviousTimePartition = Duration.between(accumulatedRecordStartInstant, adeAggregationRecordStartInstant);
        Long currentTimePartitionNumber = aggrPreviousTimePartition.toMillis() / aggregationFixedDurationStrategyInMillis;
        return currentTimePartitionNumber.intValue();
    }

    /**
     * Calculate end instant by start instant and accumulationStrategy
     * @param startInstant start time
     * @return Instant
     */
    private Instant getEndInstant(Instant startInstant) {
        return startInstant.plusSeconds(accumulationStrategy.toDuration().getSeconds());
    }

}
