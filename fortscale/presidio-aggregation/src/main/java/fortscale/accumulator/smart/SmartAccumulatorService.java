package fortscale.accumulator.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SmartAccumulatorService: accumulate aggregated records (P or F) into map (Map<featureName, Map<smartHour, score | featureValue>>)
 */
public class SmartAccumulatorService {

    private SmartAccumulationsCache smartAccumulationsCache;
    private TimeRange timeRange;

    private static final Logger logger = Logger.getLogger(SmartAccumulatorService.class);

    public SmartAccumulatorService(SmartAccumulationsCache smartAccumulationsCache, TimeRange timeRange) {
        this.smartAccumulationsCache = smartAccumulationsCache;
        this.timeRange = timeRange;
    }


    /**
     * Accumulate smart records
     *
     * @param smartRecords smart record
     */
    public void accumulate(List<SmartRecord> smartRecords) {

        for (SmartRecord smartRecord : smartRecords) {
            String contextId = smartRecord.getContextId();
            AccumulatedSmartRecord accumulatedSmartRecord = smartAccumulationsCache.getAccumulatedRecord(contextId);

            Instant smartStartInstant = smartRecord.getStartInstant();
            Instant smartEndInstant = smartRecord.getEndInstant();
            if (!validateInstants(smartStartInstant, smartEndInstant)) {
                logger.error(String.format("The %s start instant or %s end instant of smart record does not match the %s timeRange", smartStartInstant.toString(), smartEndInstant.toString(), timeRange.toString()));
                continue;
            }

            if (accumulatedSmartRecord == null) {
                accumulatedSmartRecord = new AccumulatedSmartRecord(timeRange.getStart(), timeRange.getEnd(), contextId, smartRecord.getFeatureName());
            }

            Set<Integer> activityTime = accumulatedSmartRecord.getActivityTime();
            int smartHourOfInstant = getHourOfInstant(smartStartInstant);
            activityTime.add(smartHourOfInstant);

            Map<String, Map<Integer, Double>> aggregatedFeatureEventsValuesMap = accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap();

            for (AdeAggregationRecord adeAggregationRecord : smartRecord.getAggregationRecords()) {
                fillAggregatedFeatureEventsValues(adeAggregationRecord, aggregatedFeatureEventsValuesMap, smartHourOfInstant);
                smartAccumulationsCache.storeAccumulatedRecords(contextId, accumulatedSmartRecord);
            }
        }

    }

    /**
     * Fill the aggregatedFeatureEventsValuesMap with aggregatedFeature
     * If score or value
     *
     * @param adeAggregationRecord             aggregation record
     * @param aggregatedFeatureEventsValuesMap Map<featureName, Map<hour, score | featureValue>>
     * @param smartHourOfInstant               smart hour of start instant
     */
    private void fillAggregatedFeatureEventsValues(AdeAggregationRecord adeAggregationRecord, Map<String, Map<Integer, Double>> aggregatedFeatureEventsValuesMap, int smartHourOfInstant) {

        String featureName = adeAggregationRecord.getFeatureName();
        Map<Integer, Double> hourToScoreMap = aggregatedFeatureEventsValuesMap.get(featureName);
        if (hourToScoreMap == null) {
            hourToScoreMap = new HashMap<>();
        }

        //create add aggregatedFeature record to map if score or value greater that 0.
        AggregatedFeatureType aggregatedFeatureType = adeAggregationRecord.getAggregatedFeatureType();
        if (aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION)) {
            double value = adeAggregationRecord.getFeatureValue();
            if (value > 0) {
                hourToScoreMap.put(smartHourOfInstant, value);
            }
        } else if (aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION)) {
            double score = ((ScoredFeatureAggregationRecord) adeAggregationRecord).getScore();
            if (score > 0) {
                hourToScoreMap.put(smartHourOfInstant, score);
            }
        }

        if(!hourToScoreMap.isEmpty()) {
            aggregatedFeatureEventsValuesMap.put(featureName, hourToScoreMap);
        }
    }


    /**
     * Get hour of instant
     * e.g: 2017-05-12T20:30:10.00Z => 20
     *
     * @param StartInstant
     */
    private int getHourOfInstant(Instant StartInstant) {
        return LocalDateTime.ofInstant(StartInstant, ZoneId.of("UTC")).getHour();
    }


    /**
     * Validate that start and end instants contained in timeRange
     *
     * @param startInstant
     * @param endInstant
     * @return boolean
     */
    private boolean validateInstants(Instant startInstant, Instant endInstant) {
        return (startInstant.compareTo(timeRange.getStart()) >= 0 || endInstant.compareTo(timeRange.getEnd()) < 0);
    }


}
