package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RegexScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "regex-scorer";

    @JsonProperty("regex")
    private Pattern regexPattern;
    @JsonProperty("regex-field-name")
    private String featureFieldName;

    public RegexScorerConf(@JsonProperty("name") String name,
                           @JsonProperty("regex") Pattern regexPattern,
                           @JsonProperty("regex-field-name") String featureFieldName) {
        super(name);
        this.regexPattern = regexPattern;
        this.featureFieldName = featureFieldName;
    }


    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public String getFeatureFieldName() {
        return featureFieldName;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
