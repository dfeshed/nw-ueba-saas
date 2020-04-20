package fortscale.ml.scorer;

import fortscale.ml.scorer.config.ScoreExponentialStepsMappingConf;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.TreeMap;

/**
 * https://docs.google.com/spreadsheets/d/1kslMCoHfuG5RVXxXsqrEPYEPZpbYdlqara-kbe3W-rk/edit#gid=0
 *
 * Gaussian scores mapped to 0-100 scores by exponential function:
 * 1. we decrease the area with constant steps in order to avoid sharp increase at the edge:
 *  a. starting point of gaussian score is 2sd = 0.9544 (probability: 1 - 0.9544)
 *  b. we decrease the probability by 1.5 steps and increase the mapped score by constant step.
 * 2.Function between steps is: mappedScoreStep * numOfSteps
 */
public class ScoreExponentialStepsMapper extends AbstractScoreMapper {
    private ScoreExponentialStepsMappingConf scoreMappingConf;

    public ScoreExponentialStepsMapper(String name, Scorer baseScorer, ScoreExponentialStepsMappingConf scoreMappingConf) {
        super(name, baseScorer);
        Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    protected double mapScore(double score) {
        double probability = 1 - (score / 100);

        if (probability > scoreMappingConf.getProbabilityStartingPoint()) {
            return 0;
        }

        double mappedScoreStep = scoreMappingConf.MAX_MAPPED_SCORE_DEFAULT / scoreMappingConf.getAmountOfSteps();
        double numOfSteps = Math.log(scoreMappingConf.getProbabilityStartingPoint() / probability) /
                Math.log(scoreMappingConf.getProbabilityExponentialStep());

        if (numOfSteps > scoreMappingConf.getAmountOfSteps()) {
            return scoreMappingConf.MAX_MAPPED_SCORE_DEFAULT;
        }

        return mappedScoreStep * numOfSteps;
    }
}
