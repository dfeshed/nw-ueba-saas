package fortscale.ml.scorer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;


public class ScoreExponentialStepsMapping {
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ScoreExponentialStepsMappingConf {
        private static final double BASE_DEFAULT = 1.5;
        private static final double MIN_SCORE = 0;
        private static final double SCORE_STEP = 5;
        private static final double MIN_PROBABILITY = 1 - 0.954499736103642;
        private static final double PROBABILITY_STEP = 1.5;
        public static final double SCORE_FACTOR = 0.49;
        public static final double MAX_SCORE = 100.0;

        private double minScore = MIN_SCORE;
        private double scoreStep = SCORE_STEP;
        private double minProbability = MIN_PROBABILITY;
        private double probabilityStep = PROBABILITY_STEP;
        private double base = BASE_DEFAULT;
        private double scoreFactor = SCORE_FACTOR;
        private double maxScore = MAX_SCORE;

        public ScoreExponentialStepsMappingConf() {
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

        public double getMinProbability() {
            return minProbability;
        }

        public void setMinProbability(double minProbability) {
            this.minProbability = minProbability;
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
    }

    private ScoreExponentialStepsMapping() {
    }

    public static double mapScore(double score, ScoreExponentialStepsMappingConf scoreExponentialStepsMappingConf) {

        double probability = 1 - score / 100;
        double fromProbability = scoreExponentialStepsMappingConf.getMinProbability();

        if (probability > fromProbability) {
            return 0;
        }

        double untilProbability = scoreExponentialStepsMappingConf.getMinProbability() / scoreExponentialStepsMappingConf.getProbabilityStep();
        double untilMappedScore = scoreExponentialStepsMappingConf.getMinScore() + scoreExponentialStepsMappingConf.getScoreStep();

        while (probability < untilProbability) {
            fromProbability = untilProbability;
            untilProbability = untilProbability / scoreExponentialStepsMappingConf.getProbabilityStep();
            untilMappedScore += scoreExponentialStepsMappingConf.getScoreStep();
            untilMappedScore = (untilMappedScore > scoreExponentialStepsMappingConf.getMaxScore()) ? scoreExponentialStepsMappingConf.getMaxScore() : untilMappedScore;
        }

        double fromScore = (1 - fromProbability) * 100;
        double untilScore = (1 - untilProbability) * 100;

        double ret = untilMappedScore * Math.pow(scoreExponentialStepsMappingConf.getBase(), score - fromScore) /
                Math.pow(scoreExponentialStepsMappingConf.getBase(), (untilScore - fromScore));

        return Math.ceil(ret - scoreExponentialStepsMappingConf.getScoreFactor());
    }
}
