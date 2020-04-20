package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SimpleTestScorerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "simple-test-scorer";

    private Double score;

    public SimpleTestScorerConf(@JsonProperty("name") String name,
                              @JsonProperty("score") Double score) {
        super(name);
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public String getJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", getName());
        jsonObject.put("score", getScore());
        jsonObject.put("type", SCORER_TYPE);
        return jsonObject.toJSONString();
    }
}
