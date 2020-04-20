package presidio.output.processor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "classificationName",
        "priority"
})
public class ClassificationConfig {

    @JsonProperty("classificationName")
    private String classificationName;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("classificationName")
    public String getClassificationName() {
        return classificationName;
    }

    @JsonProperty("classificationName")
    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }


    @JsonProperty("priority")
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonProperty("priority")
    public int getPriority() {
        return priority;
    }
}
