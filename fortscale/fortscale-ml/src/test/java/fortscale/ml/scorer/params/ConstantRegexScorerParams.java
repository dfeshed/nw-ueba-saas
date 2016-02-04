package fortscale.ml.scorer.params;

import fortscale.ml.scorer.ConstantRegexScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConstantRegexScorerConf;
import fortscale.ml.scorer.config.IScorerConf;

import java.util.regex.Pattern;

public class ConstantRegexScorerParams implements ScorerParams {
    private String name = "scorer-name";
    private String regexPattern = ".*";
    private String regexFieldName = "feature-field-name";
    private Integer constantScore = 50;

    public String getName() {
        return name;
    }

    public ConstantRegexScorerParams setName(String name) {
        this.name = name;
        return this;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public ConstantRegexScorerParams setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
        return this;
    }

    public String getRegexFieldName() {
        return regexFieldName;
    }

    public ConstantRegexScorerParams setRegexFieldName(String regexFieldName) {
        this.regexFieldName = regexFieldName;
        return this;
    }

    public int getConstantScore() {
        return constantScore;
    }

    public ConstantRegexScorerParams setConstantScore(int constantScore) {
        this.constantScore = constantScore;
        return this;
    }

    @Override
    public Scorer getScorer() {
        return new ConstantRegexScorer(name, regexFieldName, Pattern.compile(regexPattern), constantScore);
    }

    public String getScorerConfJsonString() {
        String res = "{\"type\":\"" + ConstantRegexScorerConf.SCORER_TYPE + "\"";
        res += (name == null ? "" : ",\"name\":\"" + name + "\"");
        res += (regexFieldName == null ? "" : ",\"regex-field-name\":\"" + regexFieldName + "\"");
        res += (regexPattern == null ? "" : ",\"regex\":\"" + regexPattern + "\"");
        res += (constantScore == null ? "" : ",\"constant-score\":" + constantScore);
        res += "}";
        return res;
    }

    public IScorerConf getScorerConf() {
        return new ConstantRegexScorerConf(name, regexPattern, regexFieldName, constantScore);
    }
}
