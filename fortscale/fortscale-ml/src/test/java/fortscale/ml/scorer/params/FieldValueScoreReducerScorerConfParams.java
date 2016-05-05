package fortscale.ml.scorer.params;

import fortscale.common.event.Event;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.FieldValueScoreLimiter;
import fortscale.ml.scorer.FieldValueScoreReducerScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.FieldValueScoreReducerScorerConf;
import fortscale.ml.scorer.config.IScorerConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldValueScoreReducerScorerConfParams implements ScorerParams {

    static class SimpleScorer implements Scorer {

        private Double score = 50.0;
        private String name = "Base Scorer";

        public SimpleScorer() {
        }

        public SimpleScorer(Double score, String name) {
            this.score = score;
            this.name = name;
        }

        public String getName() {

            return name;
        }

        public Double getScore() {
            return score;
        }


        @Override
        public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
            return new FeatureScore("SimpleTestScorer", score);
        }
    }

    String name = "default name";
    ScorerParams baseScorerParams = new ConstantRegexScorerParams();
    List<FieldValueScoreLimiter> limiters = new ArrayList<>();
    Scorer baseScorer = new SimpleScorer();

    public FieldValueScoreReducerScorerConfParams() {

        // Initialize limiters with some values.
        FieldValueScoreLimiter limiter = new FieldValueScoreLimiter();
        limiter.setFieldName("country");
        Map<String, Integer> valueToMaxScoreMap = new HashMap<>();
        valueToMaxScoreMap.put("United States", 85);
        limiter.setValueToMaxScoreMap(valueToMaxScoreMap);
        List<FieldValueScoreLimiter> limiterList = new ArrayList<>();
        limiterList.add(limiter);
        this.limiters = limiterList;
    }

    public String getName() {
        return name;
    }

    public FieldValueScoreReducerScorerConfParams setName(String name) {
        this.name = name;
        return this;
    }

    public ScorerParams getBaseScorerParams() {
        return baseScorerParams;
    }

    public FieldValueScoreReducerScorerConfParams setBaseScorerParams(ScorerParams baseScorerParams) {
        this.baseScorerParams = baseScorerParams;
        return this;
    }

    public List<FieldValueScoreLimiter> getLimiters() {
        return limiters;
    }

    public FieldValueScoreReducerScorerConfParams setLimiters(List<FieldValueScoreLimiter> limiters) {
        this.limiters = limiters;
        return this;
    }

    public String getLimitersAsJsonString() {
        if (limiters == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder("[");
        boolean first1 = true;
        for (FieldValueScoreLimiter limiter : limiters) {
            if (first1) {
                first1 = false;
            } else {
                sb.append(",");
            }
            sb.append("{\"fieldName\":\"").append(limiter.getFieldName()).append("\", \"valueToMaxScoreMap\":{");
            boolean first2 = true;
            for (Map.Entry<String, Integer> entry : limiter.getValueToMaxScoreMap().entrySet()) {
                if (first2) {
                    first2 = false;
                } else {
                    sb.append(",");
                }
                sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            }
            sb.append("}}");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getScorerConfJsonString() {
        StringBuilder sb = new StringBuilder("{");
        boolean needComma = false;
        if (name != null) {
            sb.append("\"name\":\"").append(name).append("\"");
            needComma = true;
        }
        if (needComma) {
            sb.append(", ");
        }
        sb.append("\"type\":\"").append(FieldValueScoreReducerScorerConf.SCORER_TYPE).append("\"");
        if (baseScorerParams != null) {
            sb.append(",\"base-scorer\":").append(baseScorerParams.getScorerConfJsonString());
        }
        if (limiters != null) {
            sb.append(", \"limiters\":").append(getLimitersAsJsonString());
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public IScorerConf getScorerConf() {
        return new FieldValueScoreReducerScorerConf(name, baseScorerParams.getScorerConf(), limiters);
    }

    public Scorer getBaseScorer() {
        return baseScorer;
    }

    public FieldValueScoreReducerScorerConfParams setBaseScorer(Scorer baseScorer) {
        this.baseScorer = baseScorer;
        return this;
    }

    public FieldValueScoreReducerScorerConfParams setBaseScorer(Double score) {
        this.baseScorer = new SimpleScorer(score, "Dummy Scorer");
        return this;
    }

    public FieldValueScoreReducerScorer getScorer() {
        return new FieldValueScoreReducerScorer(name, baseScorer, limiters);
    }
}
