package fortscale.ml.model.retriever.smart_data;

import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 30/08/2017.
 */
public class SmartAccumulationFlattener {
    protected static final Logger logger = Logger.getLogger(SmartAccumulationFlattener.class);

    public static List<SmartAggregatedRecordDataContainer> flattenSmartRecordToSmartAggrData(List<AccumulatedSmartRecord> accumulatedSmartRecords) {
        List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainerList = new ArrayList<>();
        for (AccumulatedSmartRecord accumulatedSmartRecord : accumulatedSmartRecords) {
            Instant startInstant = accumulatedSmartRecord.getStartInstant();


            for (Integer activityTime : accumulatedSmartRecord.getActivityTime()) {
                Map<String, Double> featureNameToScore = new HashMap<>();
                for (Map.Entry<String, Map<Integer, Double>> aggrFeature : accumulatedSmartRecord.getAggregatedFeatureEventsValuesMap().entrySet()) {

                    String featureName = aggrFeature.getKey();
                    // todo: add this info into the smart record
                    if (!featureName.toLowerCase().endsWith("hourly")) {
                        logger.warn("featureName={} does contain hourly suffix", featureName);
                    }

                    Double activityTimeScore = aggrFeature.getValue().get(activityTime);

                    if (activityTimeScore == null) {
                        logger.debug("score does not exists for aggrFeature={} at activityTime={} setting to 0", featureName, activityTime);
                    } else {
                        logger.debug("score={} for aggrFeature={} at activityTime={}", activityTimeScore, featureName, activityTime);
                        featureNameToScore.put(featureName, activityTimeScore);
                    }

                }

                // todo: make more generic (duration should not be only of hours)
                smartAggregatedRecordDataContainerList.add(new SmartAggregatedRecordDataContainer(startInstant.plus(Duration.ofHours(activityTime)), featureNameToScore));
            }
        }
        return smartAggregatedRecordDataContainerList;
    }
}
