package fortscale.ml.model.builder.smart_weights;

import fortscale.ml.model.retriever.smart_data.SmartAggregatedRecordDataContainer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/pages/viewpage.action?pageId=75071492
 */
public class ClustersContributionsSimulator {
    private static final Logger logger = Logger.getLogger(ClustersContributionsSimulator.class);

    private static final int NUM_OF_ALERTS_PER_DAY_IN_SIMULATION = 10;
    private static final double EPSILON = 0.000001;

    private SmartWeightsScorerAlgorithm scorerAlgorithm;
    private boolean useWeightForContributionCalculation = true;

    public ClustersContributionsSimulator(SmartWeightsScorerAlgorithm scorerAlgorithm) {
        this.scorerAlgorithm = scorerAlgorithm;
    }

    public ClustersContributionsSimulator(SmartWeightsScorerAlgorithm scorerAlgorithm, boolean useWeightForContributionCalculation) {
        this.scorerAlgorithm = scorerAlgorithm;
        this.useWeightForContributionCalculation = useWeightForContributionCalculation;
    }


    /**
     * Simulate which of the given smart events would generate an alert (if using the given clusterConfs),
     * and return the contribution of every cluster to the alerts.
     */
    public Map<ClusterConf, Double> simulate(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers,
                                             List<ClusterConf> clusterConfs) {
        List<SmartAggregatedRecordDataContainer> topSmartAggregatedRecordDataContainers = calcTopSmartEvents(
                smartAggregatedRecordDataContainers,
                clusterConfs,
                NUM_OF_ALERTS_PER_DAY_IN_SIMULATION
        );
        if (logger.isDebugEnabled()) {
            List<Double> sortedSmartValues = calcSmartValues(topSmartAggregatedRecordDataContainers, clusterConfs)
                    .map(Pair::getRight)
                    .sorted()
                    .collect(Collectors.toList());
            logger.debug("lowest smart value - {}, highest smart value - {}",
                    sortedSmartValues.get(0), sortedSmartValues.get(sortedSmartValues.size() - 1));
        }
        return calcContributions(topSmartAggregatedRecordDataContainers, clusterConfs);
    }

    /**
     * Given {@link List<SmartAggregatedRecordDataContainer>), calculate how much every cluster of the given {@link List<ClusterConf>}
     * contributes to these SmartAggregatedRecordDataContainer. This is done by calculating how much every cluster contributes
     * to every single {@link SmartAggregatedRecordDataContainer}, and then making a weighted average of the contributions over all
     * of the smart events data (where the weight is the smart event value, which is of course a function of
     * the clusters). The weighted average gaurantees that the more important an smart event is (where
     * important == high smart value, since we assume the higher the smart value is, the
     * higher the alert's score will be), the more significant the contributions calcullated from it are.
     * @param smartAggregatedRecordDataContainers the data to evaluate the contributions upon.
     * @param clusterConfList the clusters to evaluate.
     * @return a map from each cluster to its contribution. It's normalized such that the sum of the contributions is 1.
     */
    Map<ClusterConf, Double> calcContributions(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers,
                                                List<ClusterConf> clusterConfList) {
        Map<ClusterConf, Double> clusterConfsToContribution = calcSmartValues(smartAggregatedRecordDataContainers, clusterConfList)
                // calculate the contributions of every cluster for the given SmartAggregatedRecordDataContainer
                .map(smartDataAndValue -> normalizeMapValuesToSumTo(
                        calcContributions(smartDataAndValue.getLeft(), clusterConfList),
                        // the contributions should sum to the smart value (the bigger the value, it means
                        // the contributions generated from this smart data should have more weight)
                        smartDataAndValue.getRight()
                ))
                // add all the contributions
                .reduce((clusterConfsToContribution1, clusterConfsToContribution2) -> Stream.concat(
                        clusterConfsToContribution1.entrySet().stream(),
                        clusterConfsToContribution2.entrySet().stream()
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum)))
                .orElseGet(Collections::emptyMap);
        Map<ClusterConf, Double> contributions = normalizeMapValuesToSumTo(clusterConfsToContribution, 1);

        logger.debug("contributions:");
        contributions.forEach((clusterConf, contribution) -> logger.debug("{}: {}", clusterConf.toString(), contribution));

        return contributions;
    }

    /**
     * Given a {@link SmartAggregatedRecordDataContainer), calculate how much every cluster of the given {@link List<ClusterConf>}
     * contributes to it. This is done by calculating for every cluster how much will it contibute to the
     * smart value.
     * There are 2 option to calculate contribution.
     * One using the cluster weight and one giving the same weight to each cluster (which is essentially the maximal score
     * of the aggregated features participating in the cluster).
     * This is configured by the member useWeightForContributionCalculation.
     * The default configuration is to use the weight.
     * The reason why one might use a hypothetical equals weight {@link  List<ClusterConf>}
     * instead of the given {@link  List<ClusterConf>} is that once the user will see the alert
     * in our product, he won't have any clue what are the weights. All he sees is the indicators and their scores.
     * @param smartAggregatedRecordDataContainer the data to evaluate the contributions upon.
     * @param clusterConfList the clusters to evaluate.
     * @return a map from each cluster to its contribution. It's normalized such that the sum of the contributions is 1.
     */
    private Map<ClusterConf, Double> calcContributions(SmartAggregatedRecordDataContainer smartAggregatedRecordDataContainer,
                                                        List<ClusterConf> clusterConfList) {
        Map<ClusterConf, Double> clusterConfToMaxScore = clusterConfList.stream()
                // create a map
                .collect(Collectors.toMap(
                        // from every clusterConfs
                        Function.identity(),
                        // to the maximal score of the features participating in the clusterConfs
                        clusterConfs -> {
                            Double maxScore = SmartWeightsScorerAlgorithm.translateClusterConfsToCluster
                                    (
                                            smartAggregatedRecordDataContainer.getSmartAggregatedRecordsData(),
                                            clusterConfs
                                    )
                                    .getMaxScore();
                            return maxScore == null ? 0 : useWeightForContributionCalculation ? scorerAlgorithm.calculateScoreValue(maxScore, clusterConfs.getWeight()) : maxScore;
                        }
                ));
        return normalizeMapValuesToSumTo(clusterConfToMaxScore, 1);
    }

    /**
     * Alter the given map such that the sum of its values will be desiredSum.
     * If the map is empty or all of its values are 0, then no normalization will occur
     * (since the normalization is implemented by using multiplication).
     * @return the altered map (which is changed in place).
     */
    private <T> Map<T, Double> normalizeMapValuesToSumTo(Map<T, Double> m, double desiredSum) {
        double sum = m.values().stream().collect(Collectors.summingDouble(v -> v));
        if (Math.abs(sum) > EPSILON) {
            m.forEach((clusterConfs, contribution) -> m.put(clusterConfs, contribution * desiredSum / sum));
        }
        return m;
    }

    /**
     * Get the top smarts from every day by mapping a SmartAggregatedRecordDataContainer to its smart value.
     * @param smartAggregatedRecordDataContainers the smarts to pull from, potentially spread across multiple days.
     * @param clusterConfList  used for calculating the smart value.
     * @param kPerDay the number of top smarts to pull from each day.
     */
    List<SmartAggregatedRecordDataContainer> calcTopSmartEvents(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers,
                                                                List<ClusterConf> clusterConfList,
                                                                int kPerDay) {
        return groupSmartAggregatedRecordDataContainerByDay(smartAggregatedRecordDataContainers).values().stream()
                // get the top from every day
                .flatMap(aggregatedRecordDataContainers -> calcTopSmartsInOneDay(
                        aggregatedRecordDataContainers,
                        clusterConfList,
                        kPerDay
                ).stream())
                // and combine them to a single list
                .collect(Collectors.toList());
    }

    /**
     * Given a list of {@link SmartAggregatedRecordDataContainer} potentially spread across multiple days,
     * create a mapping from day to the {@link List<SmartAggregatedRecordDataContainer>} of that day.
     */
    private Map<Long, List<SmartAggregatedRecordDataContainer>> groupSmartAggregatedRecordDataContainerByDay(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers) {
        return smartAggregatedRecordDataContainers.stream()
                .collect(Collectors.groupingBy(
                        // map every smart data to its date
                        smartAggregatedRecordDataContainer -> TimestampUtils.toStartOfDay(smartAggregatedRecordDataContainer.getStartTime().toEpochMilli())
                ));
    }

    /**
     * Get the top smarts from the given day by mapping a SmartAggregatedRecordDataContainer to its smart value.
     * @param smartAggregatedRecordDataContainers the smarts to pull from. It's assumed that they belong to the same day.
     * @param clusterConfList confs used for calculating the smart value.
     * @param k the number of top smarts to pull.
     */
    private List<SmartAggregatedRecordDataContainer> calcTopSmartsInOneDay(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers,
                                                                           List<ClusterConf> clusterConfList,
                                                                           int k) {
        // create a priority queue where priority is the smart value
        PriorityQueue<Pair<SmartAggregatedRecordDataContainer, Double>> q =
                new PriorityQueue<>(Comparator.comparing(Pair::getRight));
        calcSmartValues(smartAggregatedRecordDataContainers, clusterConfList)
                .forEach(smartDataAndValue -> {
                    q.offer(smartDataAndValue);
                    if (q.size() > k) {
                        q.poll();
                    }
                });

        return q.stream()
                .map(Pair::getLeft)
                .collect(Collectors.toList());
    }

    /**
     * For every {@link SmartAggregatedRecordDataContainer} in the given {@link List<SmartAggregatedRecordDataContainer>} calculate its
     * smart value by using the given {@link List<ClusterConf>}.
     */
    private Stream<Pair<SmartAggregatedRecordDataContainer, Double>> calcSmartValues(List<SmartAggregatedRecordDataContainer> smartAggregatedRecordDataContainers,
                                                                                     List<ClusterConf> clusterConfList) {
        return smartAggregatedRecordDataContainers.stream()
                .map(smartAggregatedRecordDataContainer -> new ImmutablePair<>(
                        smartAggregatedRecordDataContainer,
                        scorerAlgorithm.calculateScore(smartAggregatedRecordDataContainer.getSmartAggregatedRecordsData(), clusterConfList).getScore()
                ));
    }


}
