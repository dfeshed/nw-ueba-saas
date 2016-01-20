package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AbstractScorerConf implements IScorerConf {
    public static final String SCORER_TYPE = "abstract-scorer";

    @JsonProperty("name")
    private String name;

    public AbstractScorerConf(@JsonProperty("name") String name) {
        Assert.isTrue(!StringUtils.isEmpty(name) && StringUtils.isNotBlank(name),"name must be provided and cannot be null or empty");
        this.name = name;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    @Override
    public String getName() {
        return name;
    }
}
