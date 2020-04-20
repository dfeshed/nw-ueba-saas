package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import org.springframework.util.Assert;

import java.util.*;

public class SMARTMaxValuesModelScorerAlgorithm {
    private int globalInfluence;
    private int maxUserInfluence;
    private int numOfPartitionUserInfluence;
    private int minNumOfUserValues;

    public SMARTMaxValuesModelScorerAlgorithm(int globalInfluence, int maxUserInfluence, int numOfPartitionUserInfluence, int minNumOfUserValues) {
        this.globalInfluence = globalInfluence;
        this.maxUserInfluence = maxUserInfluence;
        this.numOfPartitionUserInfluence = numOfPartitionUserInfluence;
        this.minNumOfUserValues = minNumOfUserValues;
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

        if(model == null){
            return calculateScoreByPriorOnly(value, priorModel);
        }

        double userInfluence = Math.min(maxUserInfluence, Math.ceil((double) model.getNumOfPartitions() / numOfPartitionUserInfluence));

        Map<Long, Double> startInstantToMaxSmartValues = model.getStartInstantToMaxSmartValue();
        double sumOfMaxValues = startInstantToMaxSmartValues.values().stream().sorted(Comparator.reverseOrder()).limit((long) userInfluence).mapToDouble(d -> d).sum();

        double sumOfMaxValuesWithoutPrior = calcSumOfMaxValueWithoutPrior(startInstantToMaxSmartValues, sumOfMaxValues, userInfluence);
        double probOfNewValueGreaterThanValue = sumOfMaxValuesWithoutPrior > 0 ? Math.pow(sumOfMaxValuesWithoutPrior / (value + sumOfMaxValuesWithoutPrior), Math.max(userInfluence, minNumOfUserValues)) : 0;

        if (globalInfluence >= 1) {
            double sumOfValuesWithPrior = sumOfMaxValues + globalInfluence * priorModel.getPrior();
            if(sumOfValuesWithPrior == 0){
                return 100;
            }
            double probOfNewValueGreaterThanValueWithPrior = Math.pow(sumOfValuesWithPrior / (value + sumOfValuesWithPrior), userInfluence + globalInfluence);
            return 100 * (1 - Math.max(probOfNewValueGreaterThanValue, probOfNewValueGreaterThanValueWithPrior));
        }

        return 100 * (1 - probOfNewValueGreaterThanValue);
    }

    private double calculateScoreByPriorOnly(double value, SMARTValuesPriorModel priorModel) {
        if (value == 0) {
            return 0;
        }

        if(globalInfluence == 0 || priorModel.getNumOfPartitions() == 0){
            return 0;
        }

        double sumOfValues = priorModel.getNumOfPartitions() * priorModel.getPrior();
        if (sumOfValues == 0){
            return 100;
        }
        double probOfNewValueGreaterThanValueWithPrior = Math.pow(sumOfValues / (value + sumOfValues), priorModel.getNumOfPartitions());
        return 100 * (1 - probOfNewValueGreaterThanValueWithPrior);
    }

    /**
     * If amount of smart values less than minNumOfUserValues => get max value and add it to sumOfMaxValues appropriate num of times.
     *
     * @param startInstantToSmartValues startInstantToSmartValues map
     * @param sumOfMaxValues sumOfMaxValues
     * @param numOfValues numOfValues
     * @return sum of max values
     */
    private double calcSumOfMaxValueWithoutPrior(Map<Long, Double> startInstantToSmartValues, double sumOfMaxValues, double numOfValues) {

        if (minNumOfUserValues > numOfValues && !startInstantToSmartValues.isEmpty()) {
            Double maxSmartValue = Collections.max(startInstantToSmartValues.values());
            sumOfMaxValues += maxSmartValue * (minNumOfUserValues - numOfValues);
        }

        return sumOfMaxValues;
    }

}
