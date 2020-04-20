package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public abstract class AbstractScorerConf implements IScorerConf {
    public static final String NULL_OR_EMPTY_NAME_ERROR_MSG = "name must be provided and cannot be null or empty";

    @JsonProperty("name")
    private String name;

    public AbstractScorerConf(@JsonProperty("name") String name) {
        Assert.hasText(name, NULL_OR_EMPTY_NAME_ERROR_MSG);
        this.name = name;
    }

    @Override
    abstract public String getFactoryName();

    @Override
    public String getName() {
        return name;
    }
}
