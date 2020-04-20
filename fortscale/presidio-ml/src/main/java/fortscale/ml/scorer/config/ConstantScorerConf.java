package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.util.Assert;

/**
 * Created by YaronDL on 1/10/2018.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ConstantScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "constant-scorer";
    private static final String INVALID_CONSTANT_SCORE_ERROR_MSG = "constantScore must be >= 0 AND <= 100: %d";

    @JsonProperty("constant-score")
    private Double constantScore;

    @JsonCreator
    public ConstantScorerConf(@JsonProperty("name") String name,
                              @JsonProperty("constant-score") Double constantScore) {
        super(name);
        Assert.notNull(constantScore, "constant score should not be null");
        assertConstantScoreValue(constantScore);
        this.constantScore = constantScore;
    }

    public Double getConstantScore() {
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

        ConstantScorerConf that = (ConstantScorerConf) o;

        return new EqualsBuilder()
                .append(this.getName(), that.getName())
                .append(this.getConstantScore(), that.getConstantScore())
                .isEquals();
    }

    private static void assertConstantScoreValue(double constantScore) {
        if(constantScore < 0 || constantScore > 100) {
            throw new IllegalArgumentException(String.format(INVALID_CONSTANT_SCORE_ERROR_MSG, constantScore));
        }
    }
}
