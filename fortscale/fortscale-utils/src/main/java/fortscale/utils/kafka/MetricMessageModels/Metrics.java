package fortscale.utils.kafka.metricMessageModels;

/**
 * Created by baraks on 4/13/2016.
 */

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class Metrics {

    public Metrics()
    {

    }
    @JsonProperty
    private Map<Object, Map<Object,Object>> additionalProperties = new HashMap<Object, Map<Object,Object>>();

    @JsonAnyGetter
    public Map<Object, Map<Object,Object>> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(Object name, Map<Object,Object> value) {
        this.additionalProperties.put(name, value);
    }
    @JsonCreator
    public Metrics(Map<Object, Map<Object,Object>> additionalProperties)
    {
        this.additionalProperties=additionalProperties;
    }

}