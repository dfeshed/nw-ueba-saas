package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.TreeMap;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ScoreExponentialStepsMappingConf {
    private static final double BASE_DEFAULT = 1.5;
    private static final double MIN_SCORE_DEFAULT = 0;
    private static final double SCORE_STEP_DEFAULT = 3;
    private static final double MAX_PROBABILITY_DEFAULT = 1 - 0.954499736103642;
    private static final double PROBABILITY_STEP_DEFAULT = 1.5;
    private static final double SCORE_FACTOR_DEFAULT = 0.5;
    private static final double MAX_SCORE_DEFAULT = 100.0;

    private double minScore = MIN_SCORE_DEFAULT;
    private double scoreStep = SCORE_STEP_DEFAULT;
    private double maxProbability = MAX_PROBABILITY_DEFAULT;
    private double probabilityStep = PROBABILITY_STEP_DEFAULT;
    private double base = BASE_DEFAULT;
    private double scoreFactor = SCORE_FACTOR_DEFAULT;
    private double maxScore = MAX_SCORE_DEFAULT;
    private TreeMap<Double, Double> scoreToMappedScoreMap;


    public ScoreExponentialStepsMappingConf() {
        scoreToMappedScoreMap = new TreeMap<>();
        double maxProbability = getMaxProbability();
        double probabilityStep = getProbabilityStep();
        double minMappedScore = getMinScore();
        double mappedScoreStep = getScoreStep();
        double minScore = (1 - maxProbability) * 100;

        scoreToMappedScoreMap.put(minScore, minMappedScore);

        while (minMappedScore < getMaxScore()) {
            double maxMappedScore = (minMappedScore + mappedScoreStep) < getMaxScore() ? (minMappedScore + mappedScoreStep) : getMaxScore();
            double minProbability = (maxProbability / probabilityStep);
            double maxScore = (1 - minProbability) * 100;
            scoreToMappedScoreMap.put(maxScore, maxMappedScore);
            minMappedScore = maxMappedScore;
            maxProbability = minProbability;
        }
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public double getScoreStep() {
        return scoreStep;
    }

    public void setScoreStep(double scoreStep) {
        this.scoreStep = scoreStep;
    }

    public double getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(double maxProbability) {
        this.maxProbability = maxProbability;
    }

    public double getProbabilityStep() {
        return probabilityStep;
    }

    public void setProbabilityStep(double probabilityStep) {
        this.probabilityStep = probabilityStep;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getScoreFactor() {
        return scoreFactor;
    }

    public void setScoreFactor(double scoreFactor) {
        this.scoreFactor = scoreFactor;
    }

    public TreeMap<Double, Double> getScoreToMappedScoreMap() {
        return scoreToMappedScoreMap;
    }
}