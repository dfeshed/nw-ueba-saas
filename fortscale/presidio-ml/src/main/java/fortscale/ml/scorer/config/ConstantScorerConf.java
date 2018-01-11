package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

/**
 * Created by YaronDL on 1/10/2018.
 */
public class ConstantScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "constant-scorer";
    public static final String INVALID_CONSTANT_SCORE_ERROR_MSG = "constantScore must be >= 0 AND <= 100: %d";

    @JsonProperty("constant-score")
    private Double constantScore;

    public ConstantScorerConf(@JsonProperty("name") String name,
                              @JsonProperty("constant-score") Double constantScore) {
        super(name);
        Assert.notNull(constantScore, "constant score should not be null");
        ConstantScorerConf.assertConstantScoreValue(constantScore);
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

        return new org.apache.commons.lang.builder.EqualsBuilder()
                .append(this.getName(), that.getName())
                .append(this.getConstantScore(), that.getConstantScore())
                .isEquals();
    }

    public static void assertConstantScoreValue(double constantScore) {
        if(constantScore < 0 || constantScore > 100) {
            throw new IllegalArgumentException(String.format(INVALID_CONSTANT_SCORE_ERROR_MSG, constantScore));
        }
    }
}
