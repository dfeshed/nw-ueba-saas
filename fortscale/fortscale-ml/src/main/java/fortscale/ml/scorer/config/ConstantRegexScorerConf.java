package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ConstantRegexScorerConf extends RegexScorerConf{
    public static final String SCORER_TYPE = "constant-regex-scorer";

    @JsonProperty("constant-score")
    private int constantScore;

    public ConstantRegexScorerConf(@JsonProperty("name") String name,
                                   @JsonProperty("regex") Pattern regexPattern,
                                   @JsonProperty("regex-field-name") String featureFieldName,
                                   @JsonProperty("constant-score") int constantScore) {
        super(name, regexPattern, featureFieldName);
        this.constantScore = constantScore;
    }

    public int getConstantScore() {
        return constantScore;
    }
}
