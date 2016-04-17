package fortscale.utils.kafka.metricMessageModels;

/**
 * Created by baraks on 4/13/2016.
 */

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class Metrics {

    public Metrics() {

    }


    @JsonProperty
    private String data;

    @JsonProperty
    private Map<String, Map<String, Object>> additionalProperties = new HashMap<String, Map<String, Object>>();

    @JsonAnyGetter
    public Map<String, Map<String, Object>> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Map<String, Object> value) {
        this.additionalProperties.put(name, value);
    }

    @JsonCreator
    public Metrics(Map<String, Map<String, Object>> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @JsonAnyGetter
    public String getData() {
        return data;
    }

    @JsonAnySetter
    public void setData(String data) {
        this.data = data;
    }

}