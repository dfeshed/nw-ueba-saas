package fortscale.accumulator;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by maria_dorohin on 7/30/17.
 */
public class AccumulatorService implements Accumulator {

    private AccumulationsCache accumulationsInMemory;
    private FixedDurationStrategy fixedDurationStrategy;

    public AccumulatorService(AccumulationsCache accumulationsInMemory, FixedDurationStrategy fixedDurationStrategy) {
        this.accumulationsInMemory = accumulationsInMemory;
        this.fixedDurationStrategy = fixedDurationStrategy;
    }

    @Override
    public void accumulate(List<AdeAggregationRecord> adeAggregationRecords) {

        for (AdeAggregationRecord adeAggregationRecord : adeAggregationRecords) {
            String context = AdeContextualAggregatedRecord.getAggregatedFeatureContextId(adeAggregationRecord.getContext());
            String featureName = adeAggregationRecord.getFeatureName();

            AccumulatedAggregationFeatureRecord accumulatedRecord = accumulationsInMemory.getAccumulatedRecord(featureName, context);

            Instant adeAggregationRecordStartInstant = adeAggregationRecord.getStartInstant();
            if (accumulatedRecord == null) {
                Instant startInstant = TimeService.floorTime(adeAggregationRecordStartInstant, fixedDurationStrategy.toDuration());
                Instant endInstant = getEndInstant(startInstant);
                accumulatedRecord = new AccumulatedAggregationFeatureRecord(startInstant, endInstant, context, featureName);
            }
            int adeAggregationRecordStartHour = LocalDateTime.ofInstant(adeAggregationRecordStartInstant, ZoneOffset.UTC).getHour();
            accumulatedRecord.getAggregatedFeatureValues().put(adeAggregationRecordStartHour,adeAggregationRecord.getFeatureValue());
            accumulationsInMemory.storeAccumulatedRecords(featureName, context, accumulatedRecord);
        }

    }

    /**
     * Calculate end instant by start instant and fixedDurationStrategy
     * @param startInstant start time
     * @return Instant
     */
    private Instant getEndInstant(Instant startInstant) {
        return startInstant.plusSeconds(fixedDurationStrategy.toDuration().getSeconds());
    }

}
