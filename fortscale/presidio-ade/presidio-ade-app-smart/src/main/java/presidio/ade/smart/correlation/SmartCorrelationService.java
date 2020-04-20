package presidio.ade.smart.correlation;

import fortscale.smart.SmartUtil;
import fortscale.smart.record.conf.SmartRecordConf;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;
import java.util.stream.Collectors;


public class SmartCorrelationService {
    private FeatureCorrelationAlgorithm featureCorrelationAlgorithm;


    public SmartCorrelationService(SmartRecordConf smartRecordConf) {
        CorrelationForest correlationForest = new CorrelationForest(smartRecordConf.getTrees());
        FullCorrelationSet fullCorrelationSet = new FullCorrelationSet(smartRecordConf.getFullCorrelations());
        featureCorrelationAlgorithm = new FeatureCorrelationAlgorithm(correlationForest, fullCorrelationSet);
    }


    /**
     * Update scores of correlated aggregation records by tree correlation and full correlation.
     *
     * @param smartRecords smartRecords
     */
    public void updateCorrelatedFeatures(Collection<SmartRecord> smartRecords) {
        for (SmartRecord smartRecord : smartRecords) {
            Map<String, FeatureCorrelation> descSortedFeatureCorrelations = calculateSmartFeaturesCorrelations(smartRecord);
            updateSmartAggregationRecord(smartRecord, descSortedFeatureCorrelations);
        }
    }

    /**
     * calculate smartFeatures correlations
     * @param smartRecord smartRecord
     * @return desc sorted FeatureCorrelations
     */
    private Map<String, FeatureCorrelation> calculateSmartFeaturesCorrelations(SmartRecord smartRecord) {
        Map<String, FeatureCorrelation> featureCorrelations = new HashMap<>();

        List<SmartAggregationRecord> smartAggregationRecords = smartRecord.getSmartAggregationRecords();
        smartAggregationRecords.forEach(smartAggregationRecord -> {
            AdeAggregationRecord aggregationRecord = smartAggregationRecord.getAggregationRecord();
            Double score = SmartUtil.getAdeAggregationRecordScore(aggregationRecord);
            FeatureCorrelation featureCorrelation = new FeatureCorrelation(aggregationRecord.getFeatureName(), score);
            featureCorrelations.put(featureCorrelation.getName(), featureCorrelation);
        });

        Map<String, FeatureCorrelation> descSortedFeatureCorrelations = featureCorrelations.entrySet().stream()
                .sorted(Comparator.comparing(c -> (-1) * c.getValue().getScore())).collect(Collectors.toMap
                        (Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        featureCorrelationAlgorithm.updateCorrelatedFeatures(descSortedFeatureCorrelations);
        return descSortedFeatureCorrelations;
    }

    /**
     * update smartAggregationRecords by sorted FeatureCorrelations
     * @param smartRecord smartRecord
     * @param descSortedFeatureCorrelations desc sorted FeatureCorrelations
     */
    private void updateSmartAggregationRecord(SmartRecord smartRecord, Map<String, FeatureCorrelation> descSortedFeatureCorrelations) {

        smartRecord.getSmartAggregationRecords().forEach(smartAggregationRecord -> {
            AdeAggregationRecord adeAggregationRecord = smartAggregationRecord.getAggregationRecord();

            FeatureCorrelation featureCorrelation = descSortedFeatureCorrelations.get(adeAggregationRecord.getFeatureName());

            Double correlationFactor = featureCorrelation.getCorrelationFactor();

            //If featureCorrelation exist in correlation tree or full correlation
            if(correlationFactor != null){
                Double oldScore = featureCorrelation.getScore();
                smartAggregationRecord.setCorrelationFactor(correlationFactor);
                smartAggregationRecord.setOldScore(oldScore);
                smartAggregationRecord.setCorrelationTreeName(featureCorrelation.getTreeName());
                smartAggregationRecord.setFullCorrelationName(featureCorrelation.getFullCorrelationName());
                Double newScore = oldScore * correlationFactor;
                SmartUtil.setAdeAggregationRecordScore(adeAggregationRecord, newScore);
            }
        });

    }
}
