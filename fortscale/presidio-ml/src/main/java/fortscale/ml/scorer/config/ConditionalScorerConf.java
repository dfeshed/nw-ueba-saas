package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * Created by YaronDL on 8/6/2017.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ConditionalScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "conditional-scorer";

    private IScorerConf scorer;
    private String conditionalField;
    private String conditionalValue;

    @JsonCreator
    public ConditionalScorerConf(
            @JsonProperty("name") String name,
            @JsonProperty("scorer") IScorerConf scorer,
            @JsonProperty("conditional-field") String conditionalField,
            @JsonProperty("conditional-value") String conditionalValue) {

        super(name);
        Assert.notNull(scorer, "Scorer configuration cannot be null.");
        Assert.isTrue(StringUtils.isNotBlank(conditionalField),"condition field should not be blank");
        Assert.isTrue(StringUtils.isNotBlank(conditionalValue),"condition value should not be blank");
        this.scorer = scorer;
        this.conditionalField = conditionalField;
        this.conditionalValue = conditionalValue;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public IScorerConf getScorer() {
        return scorer;
    }

    public String getConditionalField() {
        return conditionalField;
    }

    public String getConditionalValue() {
        return conditionalValue;
    }
}
