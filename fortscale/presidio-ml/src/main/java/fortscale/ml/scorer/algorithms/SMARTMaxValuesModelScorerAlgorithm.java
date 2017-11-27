package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.SMARTValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SMARTMaxValuesModelScorerAlgorithm {
    private int globalInfluence;
    private int maxUserInfluence;
    private int numOfPartitionUserInfluence;
    private int minNumOfUserValues;

    public SMARTMaxValuesModelScorerAlgorithm(int globalInfluence, int maxUserInfluence, int numOfPartitionUserInfluence, int minNumOfUserValues) {
        assertGlobalInfluence(globalInfluence);
        this.globalInfluence = globalInfluence;
        this.maxUserInfluence = maxUserInfluence;
        this.numOfPartitionUserInfluence = numOfPartitionUserInfluence;
        this.minNumOfUserValues = minNumOfUserValues;
    }

    public static void assertGlobalInfluence(int globalInfluence) {
        Assert.isTrue(globalInfluence >= 0, String.format("globalInfluence must be >= 0: %d", globalInfluence));
    }

    /**
     *
     * Get min score between probOfNewValueGreaterThanValue and probOfNewValueGreaterThanValueWithPrior.
     *
     * 1. probOfNewValueGreaterThanValueWithPrior will be selected, when user has anomaly behaviour related to himself, but has no anomaly behaviour related to organization.
     *    It means that prior value greater than user smart values.
     *    prior model will reduce the score.
     * 2. probOfNewValueGreaterThanValue will be selected, when user has anomaly behavior related to organization but not to himself.
     *    It means that user smartValues greater than prior value.
     *
     *
     * @param value new smart value
     * @param model model
     * @param priorModel priorModel
     * @return smart score
     */
    public double calculateScore(double value, SMARTMaxValuesModel model, SMARTValuesPriorModel priorModel) {
        Assert.isTrue(value >= 0, String.format("SMART value must be >= 0: %f", value));

        if (value == 0) {
            return 0;
        }

        double userInfluence = Math.min(maxUserInfluence, Math.ceil(model.getNumOfPartitions() / numOfPartitionUserInfluence));

        Map<Long, Double> startInstantToMaxSmartValues = model.getStartInstantToMaxSmartValue();
        Double sumOfMaxValues = startInstantToMaxSmartValues.values().stream().sorted(Comparator.reverseOrder()).limit((long) userInfluence).mapToDouble(d->d).sum();
        double probOfNewValueGreaterThanValue = probOfNewValueGreaterThanValue(startInstantToMaxSmartValues, sumOfMaxValues, value, userInfluence);
        double probOfNewValueGreaterThanValueWithPrior = probOfNewValueGreaterThanValueWithPrior(sumOfMaxValues, priorModel.getPrior(), value, userInfluence);

        return 100 * (1 - Math.max(probOfNewValueGreaterThanValue, probOfNewValueGreaterThanValueWithPrior));
    }

    /**
     * If amount of smart values less than minNumOfUserValues => get max value and add it to sumOfMaxValues appropriate num of times.
     *
     * @param startInstantToSmartValues startInstantToSmartValues map
     * @param sumOfMaxValues sumOfMaxValues
     * @param value new value
     * @param numOfValues numOfValues
     * @return probability that new value greater than values
     */
    private double probOfNewValueGreaterThanValue(Map<Long, Double> startInstantToSmartValues, double sumOfMaxValues, double value, double numOfValues) {

        if (minNumOfUserValues > numOfValues && !startInstantToSmartValues.isEmpty()) {
            Double maxSmartValue = Collections.max(startInstantToSmartValues.values());
            sumOfMaxValues += maxSmartValue * (minNumOfUserValues - numOfValues);
            numOfValues = minNumOfUserValues;
        }

        return sumOfMaxValues > 0 ? Math.pow(sumOfMaxValues / (value + sumOfMaxValues), numOfValues) : 0;
    }

    /**
     *
     * @param sumOfMaxValues sumOfMaxValues
     * @param prior prior
     * @param value new value
     * @param numOfValues numOfValues
     * @return probability that new value greater than values, considering prior value
     */
    private double probOfNewValueGreaterThanValueWithPrior(double sumOfMaxValues, double prior, double value, double numOfValues) {

        double sumOfValuesWithPrior = sumOfMaxValues + globalInfluence * prior;
        if (sumOfValuesWithPrior == 0) {
            return 100;
        }

        return Math.pow(sumOfValuesWithPrior / (value + sumOfValuesWithPrior),
                numOfValues + globalInfluence);
    }

}
