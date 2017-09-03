package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.utils.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/pages/viewpage.action?pageId=75071492
 */
public class WeightsModelBuilderAlgorithm {
    private static final Logger logger = Logger.getLogger(WeightsModelBuilderAlgorithm.class);

    static final double MAX_ALLOWED_WEIGHT = 0.1;
    private static final double PENALTY_LOG_BASE = 5;
    private static final double SIMULATION_WEIGHT_DECAY_FACTOR = 0.8;
    private BiFunction<List<SmartAggregatedRecordDataContainer>, Integer, AggregatedFeatureReliability> aggregatedFeatureReliabilityFactory;
    private ClustersContributionsSimulator clustersContributionsSimulator;

    public WeightsModelBuilderAlgorithm(BiFunction<List<SmartAggregatedRecordDataContainer>, Integer, AggregatedFeatureReliability> aggregatedFeatureReliabilityFactory,
                                        ClustersContributionsSimulator clustersContributionsSimulator) {
        this.aggregatedFeatureReliabilityFactory = aggregatedFeatureReliabilityFactory;
        this.clustersContributionsSimulator = clustersContributionsSimulator;
    }

    /**
     * Create a {@link ClusterConf 's} that contains clusters weights that produce "good" alerts.
     * Alerts are good if they contain features in a good balance. Features that are too noisy
     * shouldn't take over all of the alerts.
     * @param clusterConfsPrototype a prototype that defines the {@link ClusterConf}
     *                            participating in creating smart values, whose weights should be calculated,
     *                            Given no data at {@param smartAggregatedRecordDataContainers}, the weights found in the prototype will be used.
     * @param smartAggregatedRecordDataContainers the data used to estimate the best weights.
     * @param numOfContexts the number of users who triggered smartAggregatedRecordDataContainers.
     * @param numOfSimulations the number of simulations to perform in the simulations phase.
     * @return a new list of {@link ClusterConf} , or clusterConfsPrototype if no data available in smartAggregatedRecordDataContainers.
     */
    public List<ClusterConf> createWeightsClusterConfs(List<ClusterConf> clusterConfsPrototype, List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers, int numOfContexts, int numOfSimulations) {
        if(smartAggregatedRecordDataContainers.isEmpty())
        {
            logger.warn("building model from empty data");
            return clusterConfsPrototype;
        }
        // first give a penalty to every feature based on how reliable it is (it shouldn't be too noisy)
        List<ClusterConf> clusterConfs = calculateClusterConfsViaReliability(smartAggregatedRecordDataContainers, clusterConfsPrototype, numOfContexts);
        // then, after setting the initial guess, perform many simulations in order to make the initial guess better
        return calculateClusterConfsViaSimulations(smartAggregatedRecordDataContainers, clusterConfs, numOfSimulations);
    }

    private List<ClusterConf> cloneClusterConfList(List<ClusterConf> clusterConfsPrototype) {
        return clusterConfsPrototype.stream().map(ClusterConf::new).collect(Collectors.toList());
    }

    public List<ClusterConf> calculateClusterConfsViaSimulations(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers, List<ClusterConf> clusterConfsPrototype, int numOfSimulations) {
        List<ClusterConf> currSimulationClusterConfs = cloneClusterConfList(clusterConfsPrototype);
        List<ClusterConf> bestClusterConfs = clusterConfsPrototype;
        double bestMaxContribution = Double.MAX_VALUE;
        for (int iteration = 0; iteration < numOfSimulations; iteration++) {
            Map<ClusterConf, Double> clusterToContribution =
                    clustersContributionsSimulator.simulate(smartAggregatedRecordDataContainers, currSimulationClusterConfs);
            logger.debug("ClusterConfs of the current simulation:\n{}", currSimulationClusterConfs);
            logger.debug("ClusterConf to contribution:");
            clusterToContribution.forEach((key, value) -> logger.debug(
                    "{} -> {}",
                    key.toString(),
                    value
            ));
            double maxContribution = Collections.max(clusterToContribution.values());
            logger.debug("max contribution: {}", maxContribution);
            if (maxContribution < bestMaxContribution) {
                // update best result so far
                logger.debug("current simulation is better than all previous simulations.");
                bestMaxContribution = maxContribution;
                bestClusterConfs = cloneClusterConfList(currSimulationClusterConfs);
            }
            tryToImproveClusterConfs(currSimulationClusterConfs, clusterToContribution);
        }
        return bestClusterConfs;
    }

    /**
     * Update the given {@link List<ClusterConf>} according to the given contributions.
     * The goal is to make the contributions more uniform in the next simulation run.
     * We do it by decreasing the weight of the cluster(s) that contribute the most.
     */
    private void tryToImproveClusterConfs(List<ClusterConf> currSimulationClusterConfs, Map<ClusterConf, Double> clusterToContribution) {
        double maxContribution = Collections.max(clusterToContribution.values());
        currSimulationClusterConfs.stream()
                // find the worst performing clusters
                .filter(clusterConf -> clusterToContribution.get(clusterConf) == maxContribution)
                // and decrease their weight in hope the next simulation will be better
                .forEach(clusterConfConsumer -> {
                    clusterConfConsumer.setWeight(clusterConfConsumer.getWeight() * SIMULATION_WEIGHT_DECAY_FACTOR);
                    logger.debug("updating weight: {}", clusterConfConsumer.toString());
                });
    }

    /**
     * Calculate {@link ClusterConf's} that reflects how reliable the features are. A cluster composed of reliable
     * features will have big weight. Clusters containing at least one unreliable feature will have small weight
     * (this is done by defining the weight to be a function of the most unreliable feature).
     */
    public List<ClusterConf> calculateClusterConfsViaReliability(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers, List<ClusterConf> clusterConfsPrototype, int numOfContexts) {
        AggregatedFeatureReliability reliability = aggregatedFeatureReliabilityFactory.apply(
                smartAggregatedRecordDataContainers,
                numOfContexts
        );

        Map<String, Double> featureNameToPenalty = clusterConfsPrototype.stream()
                // get all the features participating in the clusterConfs
                .flatMap(clusterConf -> clusterConf.getAggregationRecordNames().stream())
                .distinct()
                .collect(Collectors.toMap(
                        // create a map from the feature name
                        Function.identity(),
                        // to its penalty, which is in the range [0, 1)
                        aggregatedFeatureEventName ->
                                normalizePenalty(reliability.calcReliabilityPenalty(aggregatedFeatureEventName))
                ));

        logger.debug("features' reliability penalties:");
        featureNameToPenalty.forEach((key, value) -> logger.debug("\t{}: {}", key, value));

        List<ClusterConf> res = cloneClusterConfList(clusterConfsPrototype);
        res                // calculate a weight for each cluster
                .forEach(clusterConf -> {
                    double maxPenalty = clusterConf.getAggregationRecordNames().stream()
                            // map each feature of the cluster to its penalty
                            .mapToDouble(featureNameToPenalty::get)
                            // and take the worst one
                            .max()
                            .getAsDouble();
                    // transform penalty to weight in the range (0.5 * MAX_ALLOWED_WEIGHT, MAX_ALLOWED_WEIGHT]
                    clusterConf.setWeight(MAX_ALLOWED_WEIGHT * (1 - maxPenalty * 0.5));
                });
        logger.debug("ClusterConfs based on reliability penalties:\n{}", res.toString());
        return res;
    }

    /**
     * Given a penalty in the range [0, ∞), transform it into a normalized penalty in the range [0, 1).
     */
    private double normalizePenalty(double penalty) {
        // penalty >= 0
        double penaltyLog = Math.log(PENALTY_LOG_BASE + penalty / 10.0) / Math.log(PENALTY_LOG_BASE);
        // penaltyLog >= 1
        double normalizedPenalty = 1.0 - 1.0 / penaltyLog;
        // 0 <= normalizedPenalty < 1
        return normalizedPenalty;
    }
}
