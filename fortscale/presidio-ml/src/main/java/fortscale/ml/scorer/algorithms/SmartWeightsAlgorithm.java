package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.smart.record.conf.ClusterConf;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 29/08/2017.
 */
public class SmartWeightsAlgorithm {

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

    public double calculateScore(List<SmartAggregatedRecordData> recordsDataContainer, List<ClusterConf> clusterConfs)
    {
        Assert.notNull(recordsDataContainer,"smart must contain aggregated feature events");
        List<Cluster> clusters = translateClustersSpecsToClusters(recordsDataContainer, clusterConfs);
        return roundToEntityEventValuePrecision(calculateEntityEventValue(clusters));
    }

    private double roundToEntityEventValuePrecision(double entityEventValue) {
        //TODO: do we really need this function?
        return Math.round(entityEventValue * 10000000) / 10000000d;
    }

    /**
     * Instantiate {@link Cluster}s containing the scores of the given {@link List<SmartAggregatedRecordData>}
     * specified by the given {@link ClusterConf}.
     */
    private List<Cluster> translateClustersSpecsToClusters(
            List<SmartAggregatedRecordData> smartAggregatedRecordData, List<ClusterConf> clusterConfList) {

        return clusterConfList.stream()
                .map(clusterConf -> translateClusterSpecsToCluster(smartAggregatedRecordData, clusterConf))
                .collect(Collectors.toList());
    }

    /**
     * Instantiate a {@link Cluster} containing the scores of the given {@link List<SmartAggregatedRecordData>}
     * specified by the given {@link ClusterConf}.
     */
    public static Cluster translateClusterSpecsToCluster(List<SmartAggregatedRecordData> smartAggregatedRecordData,
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
    private double calculateEntityEventValue(List<Cluster> clusters) {
        return clusters.stream()
                // filter clusters that all of their feature absent from the data
                .filter(cluster -> !cluster.isEmpty())
                // map each cluster to its contribution to the entity event value
                .mapToDouble(cluster -> cluster.getMaxScore() * cluster.getWeight() / 100)
                // add all of the clusters' contributions
                .sum();
    }
}
