package fortscale.ml.scorer.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * Created by YaronDL on 8/6/2017.
 */
abstract public class ConditionalScorerConf extends AbstractScorerConf{

    private IScorerConf scorer;
    private String conditionalField;


    public ConditionalScorerConf(String name, IScorerConf scorer, String conditionalField) {
        super(name);
        Assert.notNull(scorer, "Scorer configuration cannot be null.");
        Assert.isTrue(StringUtils.isNotBlank(conditionalField),"condition field should not be blank");
        this.scorer = scorer;
        this.conditionalField = conditionalField;
    }

    abstract public String getFactoryName();

    public IScorerConf getScorer() {
        return scorer;
    }

    public String getConditionalField() {
        return conditionalField;
    }

}
