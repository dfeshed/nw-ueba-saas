package fortscale.ml.scorer.algorithms;

import fortscale.ml.model.SMARTValuesModel;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

public class SMARTValuesModelScorerAlgorithm {
    private static final Logger logger = Logger.getLogger(SMARTValuesModelScorerAlgorithm.class);
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

        if(value == 0){
            return 0;
        }

        double globalPositiveValuesMean = globalModel.getNumOfPositiveValues() == 0 ? 0 : globalModel.getSumOfValues() / globalModel.getNumOfPositiveValues();
        double sumOfValues = model.getSumOfValues() + globalInfluence * globalPositiveValuesMean;
        if (sumOfValues == 0) {
            return 100;
        }

        double probOfNewValueGreaterThanValue = model.getSumOfValues() > 0 ?
                Math.pow(model.getSumOfValues() / (value + model.getSumOfValues()), model.getNumOfPositiveValues()) :
                0;
        double probOfNewValueGreaterThanValueWithPrior = Math.pow(sumOfValues / (value + sumOfValues),
                model.getNumOfPositiveValues() + globalInfluence);

        return 100 * (1 - Math.max(probOfNewValueGreaterThanValue, probOfNewValueGreaterThanValueWithPrior));
    }
}
