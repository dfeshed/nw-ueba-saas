package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.smart.SmartUtil;
import fortscale.smart.record.conf.ClusterConf;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 29/08/2017.
 */
public class SmartWeightsScorerAlgorithm {

    /**
     * Cluster represents an instantiation of a {@link ClusterConf}
     * with {@link Set <SmartAggregatedRecordData>}. While {@link ClusterConf} holds the
     * specification of which features compose it, Cluster contains specific instances of these features.
     */
    public static class Cluster {
        private Set<SmartAggregatedRecordData> aggregatedFeatureEventScores;
        private ClusterConf clusterConf;

        public Cluster(Set<SmartAggregatedRecordData> aggregatedFeatureEventScores, ClusterConf clusterConf) {
            this.aggregatedFeatureEventScores = aggregatedFeatureEventScores;
            this.clusterConf = clusterConf;
        }

        public double getWeight() {
            return clusterConf.getWeight();
        }

        public Double getMaxScore() {
            if (isEmpty()) {
                return null;
            }
            return aggregatedFeatureEventScores.stream()
                    .mapToDouble(SmartAggregatedRecordData::getScore)
                    .max()
                    .getAsDouble();
        }

        public boolean isEmpty() {
            return aggregatedFeatureEventScores.isEmpty();
        }
    }

    public double calculateScore(SmartRecord smartRecord, SmartWeightsModel smartWeightsModel){
        List<SmartAggregatedRecordData> recordsDataContainer = new ArrayList<>();
        for (AdeAggregationRecord adeAggregationRecord: smartRecord.getAggregationRecords()){
            recordsDataContainer.add(new SmartAggregatedRecordData(adeAggregationRecord.getFeatureName(), SmartUtil.getAdeAggregationRecordScore(adeAggregationRecord)));
        }

        return calculateScore(recordsDataContainer, smartWeightsModel.getClusterConfs());
    }

    public double calculateScore(List<SmartAggregatedRecordData> recordsDataContainer, List<ClusterConf> clusterConfs)
    {
        Assert.notNull(recordsDataContainer,"records data container should not be null");
        List<Cluster> clusters = translateClusterConfsToClusters(recordsDataContainer, clusterConfs);
        return roundToSmartValuePrecision(calculateSmartValue(clusters));
    }

    private double roundToSmartValuePrecision(double smartValue) {
        //TODO: do we really need this function?
        return Math.round(smartValue * 10000000) / 10000000d;
    }

    /**
     * Instantiate {@link Cluster}s containing the scores of the given {@link List<SmartAggregatedRecordData>}
     * specified by the given {@link ClusterConf}.
     */
    private List<Cluster> translateClusterConfsToClusters(
            List<SmartAggregatedRecordData> smartAggregatedRecordData, List<ClusterConf> clusterConfList) {

        return clusterConfList.stream()
                .map(clusterConf -> translateClusterConfsToCluster(smartAggregatedRecordData, clusterConf))
                .collect(Collectors.toList());
    }

    /**
     * Instantiate a {@link Cluster} containing the scores of the given {@link List<SmartAggregatedRecordData>}
     * specified by the given {@link ClusterConf}.
     */
    public static Cluster translateClusterConfsToCluster(List<SmartAggregatedRecordData> smartAggregatedRecordData,
                                                         ClusterConf clusterConf) {
        Set<SmartAggregatedRecordData> aggrFeatureEvents = new HashSet<>();
        Map<String, SmartAggregatedRecordData> aggrFeatureEventsMap = createAggrFeaturesDataMap(smartAggregatedRecordData);
        for (String aggrFeatureEventName : clusterConf.getAggregationRecordNames()) {
            SmartAggregatedRecordData aggrFeatureEvent = aggrFeatureEventsMap.get(aggrFeatureEventName);
            if (aggrFeatureEvent != null) {
                Double score = aggrFeatureEvent.getScore();
                Assert.notNull(score, String.format("Event %s must contain a score field", aggrFeatureEventName));
                aggrFeatureEvents.add(aggrFeatureEvent);
            }
        }
        return new Cluster(aggrFeatureEvents, clusterConf);
    }

    /**
     * Create a map from a {@link SmartAggregatedRecordData}'s name to itself.
     */
    private static Map<String, SmartAggregatedRecordData> createAggrFeaturesDataMap(List<SmartAggregatedRecordData> smartAggregatedRecordData) {
        return smartAggregatedRecordData.stream()
                .collect(Collectors.toMap(
                        SmartAggregatedRecordData::getFeatureName,
                        Function.identity()
                ));
    }

    /**
     * Sum the contributions made by the given {@link List<Cluster>} into a single entity event value.
     */
    private double calculateSmartValue(List<Cluster> clusters) {
        return clusters.stream()
                // filter clusters that all of their feature absent from the data
                .filter(cluster -> !cluster.isEmpty())
                // map each cluster to its contribution to the entity event value
                .mapToDouble(cluster -> cluster.getMaxScore() * cluster.getWeight() / 100)
                // add all of the clusters' contributions
                .sum();
    }
}
