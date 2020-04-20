package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class ModelInternalUniScorerConf extends ModelScorerConf {
    private IScorerConf baseScorerConf;

    @JsonCreator
    public ModelInternalUniScorerConf(@JsonProperty("name") String name,
                                      @JsonProperty("model") ModelInfo modelInfo,
                                      @JsonProperty("additional-models") List<ModelInfo> additionalModelInfos,
                                      @JsonProperty("base-scorer") IScorerConf baseScorerConf) {

        super(name, modelInfo, additionalModelInfos);
        Assert.notNull(baseScorerConf, "base score conf should not be null");
        this.baseScorerConf = baseScorerConf;
    }

    @JsonCreator
    public ModelInternalUniScorerConf(@JsonProperty("name") String name,
                                      @JsonProperty("model") ModelInfo modelInfo,
                                      @JsonProperty("base-scorer") IScorerConf baseScorerConf) {

        this(name, modelInfo, null, baseScorerConf);
    }

    public IScorerConf getBaseScorerConf() {
        return baseScorerConf;
    }
}
