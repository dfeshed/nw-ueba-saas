package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.ConstantRegexScorer;
import org.apache.commons.lang.builder.EqualsBuilder;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ConstantRegexScorerConf extends RegexScorerConf{
    public static final String SCORER_TYPE = "constant-regex-scorer";

    @JsonProperty("constant-score")
    private int constantScore;

    public ConstantRegexScorerConf(@JsonProperty("name") String name,
                                   @JsonProperty("regex") String regexPattern,
                                   @JsonProperty("regex-field-name") String featureFieldName,
                                   @JsonProperty("constant-score") int constantScore) {
        super(name, regexPattern, featureFieldName);
        ConstantRegexScorer.assertConstantScoreValue(constantScore);
        this.constantScore = constantScore;
    }

    public int getConstantScore() {
        return constantScore;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantRegexScorerConf that = (ConstantRegexScorerConf) o;

        return new EqualsBuilder()
            .append(this.getRegexFieldName(), that.getRegexFieldName())
            .append(this.getRegexPattern().toString(), that.getRegexPattern().toString())
            .append(this.getName(), that.getName())
            .append(this.getConstantScore(), that.getConstantScore())
            .isEquals();
    }
}
