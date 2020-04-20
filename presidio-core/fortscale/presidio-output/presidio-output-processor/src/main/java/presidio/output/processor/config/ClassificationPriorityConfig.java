package presidio.output.processor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"classificationPriority"})
public class ClassificationPriorityConfig {

    @JsonProperty("classificationPriority")
    private List<ClassificationConfig> classificationPriority = null;

    @JsonProperty("classificationPriority")
    public List<ClassificationConfig> getClassificationPriority() {
        return classificationPriority;
    }

    @JsonProperty("classificationPriority")
    public void setClassificationPriority(List<ClassificationConfig> classificationPriority) {
        this.classificationPriority = classificationPriority;
    }


    public ClassificationConfig getClassificationConfig(String classificationName) {
        return classificationPriority.stream().filter(priority -> priority.getClassificationName().equals(classificationName)).findFirst().get();
    }

    public ClassificationPriorityConfig(List<ClassificationConfig> classificationPriority) {
        this.classificationPriority = classificationPriority;
    }
}
