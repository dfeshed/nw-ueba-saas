package fortscale.ml.scorer.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class StringConditionalScorerConf extends ConditionalScorerConf{
    public static final String SCORER_TYPE = "string-conditional-scorer";

    private String conditionalValue;

    @JsonCreator
    public StringConditionalScorerConf(
            @JsonProperty("name") String name,
            @JsonProperty("scorer") IScorerConf scorer,
            @JsonProperty("conditional-field") String conditionalField,
            @JsonProperty("conditional-value") String conditionalValue) {
        super(name, scorer, conditionalField);
        Assert.hasText(conditionalValue,"condition value should not be blank");
        this.conditionalValue = conditionalValue;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public String getConditionalValue() {
        return conditionalValue;
    }

}
