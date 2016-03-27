package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.ml.scorer.RegexScorer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RegexScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "regex-scorer";

    @JsonProperty("regex")
    private Pattern regexPattern;
    @JsonProperty("regex-field-name")
    private String regexFieldName;

    public RegexScorerConf(@JsonProperty("name") String name,
                           @JsonProperty("regex") String regexPattern,
                           @JsonProperty("regex-field-name") String featureFieldName) {
        super(name);
        Assert.isTrue(StringUtils.isNotEmpty(featureFieldName) && StringUtils.isNotBlank(featureFieldName), RegexScorer.EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
        Assert.notNull(regexPattern, RegexScorer.NULL_REGEX_ERROR_MSG);

        this.regexPattern = Pattern.compile(regexPattern);
        this.regexFieldName = featureFieldName;
    }


    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public String getRegexFieldName() {
        return regexFieldName;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegexScorerConf that = (RegexScorerConf) o;

        return new EqualsBuilder()
                .append(this.regexFieldName, that.regexFieldName)
                .append(this.regexPattern, that.regexPattern)
                .append(this.getName(), that.getName()).isEquals();
    }

}
