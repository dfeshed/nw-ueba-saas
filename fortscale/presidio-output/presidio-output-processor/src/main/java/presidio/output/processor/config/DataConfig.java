package presidio.output.processor.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "indicators",
        "classificationPriority"
})

public class DataConfig {

    @JsonProperty("indicators")
    private List<IndicatorConfig> indicators;

    @JsonProperty("classificationPriority")
    private List<ClassificationConfig> classificationPriority;

    @JsonProperty("indicators")
    public void setIndicators(List<IndicatorConfig> indicators) {
        this.indicators = indicators;
    }

    @JsonProperty("indicators")
    public List<IndicatorConfig> getIndicators() {
        return indicators;
    }

    @JsonProperty("classificationPriority")
    public void setClassificationPriority(List<ClassificationConfig> classificationPriority) {
        this.classificationPriority = classificationPriority;
    }

    @JsonProperty("classificationPriority")
    public List<ClassificationConfig> getClassificationPriority() {
        return classificationPriority;
    }
}
