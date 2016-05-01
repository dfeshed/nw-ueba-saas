package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.SMARTValuesModel;
import org.springframework.util.Assert;

public class SMARTValuesModelScorerAlgorithm {
    private int globalInfluence;

    public SMARTValuesModelScorerAlgorithm(int globalInfluence) {
        assertGlobalInfluence(globalInfluence);
        this.globalInfluence = globalInfluence;
    }

    public static void assertGlobalInfluence(int globalInfluence) {
        Assert.isTrue(globalInfluence >= 0, String.format("globalInfluence must be >= 0: %d", globalInfluence));
    }

    public double calculateScore(double value, SMARTValuesModel model, SMARTValuesModel globalModel) {
        Assert.isTrue(value >= 0, String.format("SMART value must be >= 0: %f", value));

        double globalPositiveValuesMean = globalModel.getSumOfValues() / globalModel.getNumOfPositiveValues();
        double sumOfValues = model.getSumOfValues() + globalInfluence * globalPositiveValuesMean;
        double probOfNewValueGreaterThanValue = Math.pow(sumOfValues / (value + sumOfValues),
                model.getNumOfPositiveValues() + globalInfluence);

        return 100 * (1 - probOfNewValueGreaterThanValue);
    }
}
