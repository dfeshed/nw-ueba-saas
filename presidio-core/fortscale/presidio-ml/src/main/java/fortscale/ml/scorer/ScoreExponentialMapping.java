package fortscale.ml.scorer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;


public class ScoreExponentialMapping {
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ScoreExponentialMappingConf {
        public static final double MIN_SCORE_TO_MAP_DEFAULT = 95.0;
        public static final double MAX_SCORE_TO_MAP_DEFAULT = 100.0;
        private static final double BASE_DEFAULT = 2.0;
        private double minScoreToMap;
        private double maxScoreToMap;
        private double base;

        public ScoreExponentialMappingConf() {
            setMinScoreToMap(MIN_SCORE_TO_MAP_DEFAULT);
            setMaxScoreToMap(MAX_SCORE_TO_MAP_DEFAULT);
            setBase(BASE_DEFAULT);
        }

        public double getMinScoreToMap() {
            return minScoreToMap;
        }

        public void setMinScoreToMap(double minScoreToMap) {
            this.minScoreToMap = minScoreToMap;
        }

        public double getMaxScoreToMap() {
            return maxScoreToMap;
        }

        public void setMaxScoreToMap(double maxScoreToMap) {
            this.maxScoreToMap = maxScoreToMap;
        }

        public double getBase() {
            return base;
        }

        public void setBase(double base) {
            this.base = base;
        }
    }

    private ScoreExponentialMapping() {
    }

    public static double mapScore(double score, ScoreExponentialMappingConf scoreMappingConf) {
        if(score>=scoreMappingConf.getMaxScoreToMap()){
            return score;
        }
        double expInputVarialble = score - scoreMappingConf.getMinScoreToMap();
        if(expInputVarialble <=0){
            return 0;
        }

        double ret = scoreMappingConf.getMaxScoreToMap()*(Math.pow(scoreMappingConf.base, expInputVarialble) / Math.pow(scoreMappingConf.base, scoreMappingConf.maxScoreToMap - scoreMappingConf.minScoreToMap));
        return ret;
    }
}
