package fortscale.ml.scorer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.*;


public class ScoreExponentialStepsMapping {
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ScoreExponentialStepsMappingConf {
        private static final double BASE_DEFAULT = 1.5;
        private static final double MIN_SCORE = 0;
        private static final double SCORE_STEP = 3;
        private static final double MAX_PROBABILITY = 1 - 0.954499736103642;
        private static final double PROBABILITY_STEP = 1.5;
        public static final double SCORE_FACTOR = 0.5;
        public static final double MAX_SCORE = 100.0;

        private double minScore = MIN_SCORE;
        private double scoreStep = SCORE_STEP;
        private double maxProbability = MAX_PROBABILITY;
        private double probabilityStep = PROBABILITY_STEP;
        private double base = BASE_DEFAULT;
        private double scoreFactor = SCORE_FACTOR;
        private double maxScore = MAX_SCORE;
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

    private ScoreExponentialStepsMapping() {
    }

    public static double mapScore(double score, ScoreExponentialStepsMappingConf scoreExponentialStepsMappingConf) {

        TreeMap<Double, Double> scoreToMappedScoreMap = scoreExponentialStepsMappingConf.getScoreToMappedScoreMap();
        if (score <= scoreToMappedScoreMap.firstKey()) {
            return scoreExponentialStepsMappingConf.getMinScore();
        }

        if (score > scoreToMappedScoreMap.lastKey()) {
            return scoreExponentialStepsMappingConf.getMaxScore();
        }

        double fromMappedScore = scoreExponentialStepsMappingConf.getMinScore();
        double fromScore = (1 - scoreExponentialStepsMappingConf.getMaxProbability()) * 100;
        double toScore = fromScore;

        for (Map.Entry<Double, Double> entry : scoreToMappedScoreMap.entrySet()) {
            if (score <= entry.getKey()) {
                toScore = entry.getKey();
                break;
            }
            fromScore = entry.getKey();
            fromMappedScore = entry.getValue();
        }

        double ret = fromMappedScore + scoreExponentialStepsMappingConf.getScoreStep() *
                (Math.pow(scoreExponentialStepsMappingConf.getBase(), score) - Math.pow(scoreExponentialStepsMappingConf.getBase(), fromScore)) /
                (Math.pow(scoreExponentialStepsMappingConf.getBase(), toScore) - Math.pow(scoreExponentialStepsMappingConf.getBase(), fromScore));


        ret = Math.floor(ret + scoreExponentialStepsMappingConf.getScoreFactor());
        ret = ret > scoreExponentialStepsMappingConf.getMaxScore() ? scoreExponentialStepsMappingConf.getMaxScore() : ret;

        return ret;
    }
}
