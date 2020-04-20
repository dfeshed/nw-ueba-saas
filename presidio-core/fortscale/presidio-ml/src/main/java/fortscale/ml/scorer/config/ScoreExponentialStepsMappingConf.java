package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.junit.Assert;


@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ScoreExponentialStepsMappingConf {
    private static final double AMOUNT_OF_STEPS_DEFAULT = 25;
    private static final double PROBABILITY_STARTING_POINT_DEFAULT = 1 - 0.954499736103642;
    private static final double PROBABILITY_EXPONENTIAL_STEP_DEFAULT = 1.5;
    public static final double MAX_MAPPED_SCORE_DEFAULT = 100.0;

    private double amountOfSteps = AMOUNT_OF_STEPS_DEFAULT;
    private double probabilityStartingPoint = PROBABILITY_STARTING_POINT_DEFAULT;
    private double probabilityExponentialStep = PROBABILITY_EXPONENTIAL_STEP_DEFAULT;

    public ScoreExponentialStepsMappingConf() {
    }

    public double getAmountOfSteps() {
        return amountOfSteps;
    }

    public void setAmountOfSteps(double amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
    }

    public double getProbabilityStartingPoint() {
        return probabilityStartingPoint;
    }

    public void setProbabilityStartingPoint(double probabilityStartingPoint) {
        this.probabilityStartingPoint = probabilityStartingPoint;
    }

    public double getProbabilityExponentialStep() {
        return probabilityExponentialStep;
    }

    public void setProbabilityStep(double probabilityExponentialStep) {
        this.probabilityExponentialStep = probabilityExponentialStep;
    }

}