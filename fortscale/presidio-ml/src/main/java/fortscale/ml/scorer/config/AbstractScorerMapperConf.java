package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public abstract class AbstractScorerMapperConf extends AbstractScorerConf {
    private IScorerConf baseScorerConf;

    public AbstractScorerMapperConf(
            @JsonProperty("name") String name,
            @JsonProperty("base-scorer") IScorerConf baseScorerConf) {

        super(name);
        Assert.notNull(baseScorerConf);
        this.baseScorerConf = baseScorerConf;
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }
}
