package fortscale.ml.scorer.algorithms;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordData;
import fortscale.smart.SmartUtil;
import fortscale.smart.record.conf.ClusterConf;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class SmartWeightsScorerAlgorithm {
    public static final String CONTRIBUTIONS_FEATURE_SCORE_NAME = "contributions";
    public static final String SCORE_AND_WEIGHT_PRODUCTS_FEATURE_SCORE_NAME = "scoreAndWeightProducts";

    private double fractionalPower;
    private double minimalClusterScore;

    public SmartWeightsScorerAlgorithm(double fractionalPower, double minimalClusterScore){
        Assert.isTrue(fractionalPower > 0 && fractionalPower < 1,
                String.format("fractional power should be in the range (0,1). fractionalPower: %f", fractionalPower));
        this.fractionalPower = fractionalPower;
        this.minimalClusterScore = minimalClusterScore;
    }

    /**
     * Cluster represents an instantiation of a {@link ClusterConf} with a list of {@link SmartAggregatedRecordData}
     * objects. While {@link ClusterConf} holds the specification of which features compose it, Cluster contains
     * specific instances of these features.
     */
    public static class Cluster {
        private List<SmartAggregatedRecordData> aggregatedFeatureEventScores;
        private Double weight;
        private Double maxScore;
        private SmartAggregatedRecordData contributor;

        public Cluster(List<SmartAggregatedRecordData> aggregatedFeatureEventScores, Double weight) {
            this.aggregatedFeatureEventScores = aggregatedFeatureEventScores;
            this.weight = weight;

            for (SmartAggregatedRecordData aggregatedFeatureEventScore : aggregatedFeatureEventScores) {
                Double score = aggregatedFeatureEventScore.getScore();

                if (maxScore == null || score > maxScore) {
                    maxScore = score;
                    contributor = aggregatedFeatureEventScore;
                }
            }
        }

        public List<SmartAggregatedRecordData> getAggregatedFeatureEventScores() {
            return aggregatedFeatureEventScores;
        }

        public Double getWeight() {
            return weight;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public SmartAggregatedRecordData getContributor() {
            return contributor;
        }

        public boolean isEmpty() {
            return aggregatedFeatureEventScores.isEmpty();
        }
    }

    public FeatureScore calculateScore(SmartRecord smartRecord, SmartWeightsModel smartWeightsModel) {
        List<SmartAggregatedRecordData> recordsDataContainer = new ArrayList<>();
        for (AdeAggregationRecord adeAggregationRecord: smartRecord.getAggregationRecords()){
            recordsDataContainer.add(new SmartAggregatedRecordData(adeAggregationRecord.getFeatureName(), SmartUtil.getAdeAggregationRecordScore(adeAggregationRecord)));
        }

        return calculateScore(recordsDataContainer, smartWeightsModel.getClusterConfs());
    }

    public FeatureScore calculateScore(List<SmartAggregatedRecordData> recordsDataContainer, List<ClusterConf> clusterConfs) {
        Assert.notNull(recordsDataContainer,"records data container should not be null");
        List<Cluster> clusters = translateClusterConfsToClusters(recordsDataContainer, clusterConfs);
        return calculateSmartValue(clusters);
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
        List<SmartAggregatedRecordData> aggrFeatureEvents = new ArrayList<>();
        Map<String, SmartAggregatedRecordData> aggrFeatureEventsMap = createAggrFeaturesDataMap(smartAggregatedRecordData);
        for (String aggrFeatureEventName : clusterConf.getAggregationRecordNames()) {
            SmartAggregatedRecordData aggrFeatureEvent = aggrFeatureEventsMap.get(aggrFeatureEventName);
            if (aggrFeatureEvent != null) {
                Double score = aggrFeatureEvent.getScore();
                Assert.notNull(score, String.format("Event %s must contain a score field", aggrFeatureEventName));
                aggrFeatureEvents.add(aggrFeatureEvent);
            }
        }
        return new Cluster(aggrFeatureEvents, clusterConf.getWeight());
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
    private FeatureScore calculateSmartValue(List<Cluster> clusters) {
        if (clusters.stream().noneMatch(cluster -> !cluster.isEmpty() && cluster.getMaxScore() >= minimalClusterScore)) {
            return new FeatureScore(StringUtils.EMPTY, 0d);
        }

        double sum = 0;
        List<FeatureScore> contributions = new ArrayList<>();
        List<FeatureScore> scoreAndWeightProducts = new ArrayList<>();

        for (Cluster cluster : clusters) {
            // Filter clusters that all of their features are absent from the data
            if (!cluster.isEmpty()) {
                Double weight = cluster.getWeight();
                SmartAggregatedRecordData contributor = cluster.getContributor();
                // Map each cluster to its contribution to the smart value
                double contribution = calculateScoreValue(cluster.getMaxScore(), weight);
                // Add all of the clusters' contributions
                sum += contribution;

                for (SmartAggregatedRecordData feature : cluster.getAggregatedFeatureEventScores()) {
                    String featureName = feature.getFeatureName();
                    // Add a mapping between each feature in the cluster to its contribution to the smart value
                    // (i.e. the feature with the maximal score, also known as the contributor, is mapped to the
                    // cluster's contribution, and the rest of the features are mapped to 0)
                    contributions.add(new FeatureScore(featureName, feature.equals(contributor) ? contribution : 0));
                    // Add a mapping between each feature in the cluster to its score and weight product
                    scoreAndWeightProducts.add(new FeatureScore(featureName, calculateScoreValue(feature.getScore(), weight)));
                }
            }
        }

        return new FeatureScore(StringUtils.EMPTY, roundToSmartValuePrecision(sum), Arrays.asList(
                new FeatureScore(CONTRIBUTIONS_FEATURE_SCORE_NAME, 0d, contributions),
                new FeatureScore(SCORE_AND_WEIGHT_PRODUCTS_FEATURE_SCORE_NAME, 0d, scoreAndWeightProducts)
        ));
    }

    public double calculateScoreValue(double score, double weight) {
        score = score / 100;
        if(score > 1) {
            score = Math.pow(score, fractionalPower);
        }
        return score * weight;
    }
}
