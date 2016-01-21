package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractScorerConf implements IScorerConf {

    @JsonProperty("name")
    private String name;

    public AbstractScorerConf(@JsonProperty("name") String name) {
        Assert.isTrue(!StringUtils.isEmpty(name) && StringUtils.isNotBlank(name),"name must be provided and cannot be null or empty");
        this.name = name;
    }

    @Override
    abstract public String getFactoryName();

    @Override
    public String getName() {
        return name;
    }
}
