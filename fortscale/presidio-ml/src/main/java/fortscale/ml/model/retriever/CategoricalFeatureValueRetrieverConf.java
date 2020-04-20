package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by barak_schuster on 10/16/17.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CategoricalFeatureValueRetrieverConf extends  AbstractDataRetrieverConf {

    public static final String FACTORY_NAME = "categorical_feature_value_retriever";
    private String featureBucketConfName;
    private String featureName;

    @JsonCreator
    public CategoricalFeatureValueRetrieverConf(
            @JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
            @JsonProperty("functions") List<JSONObject> functions,
            @JsonProperty("featureBucketConfName") String featureBucketConfName,
            @JsonProperty("featureName") String featureName) {
        super(timeRangeInSeconds, functions);

        Assert.hasText(featureBucketConfName,"featureBucketConfName must be not empty");
        Assert.hasText(featureName,"featureName must be not empty");

        this.featureBucketConfName = featureBucketConfName;
        this.featureName = featureName;
    }

    @Override
    public String getFactoryName() {
        return FACTORY_NAME;
    }

    public String getFeatureBucketConfName() {
        return featureBucketConfName;
    }

    public String getFeatureName() {
        return featureName;
    }
}
