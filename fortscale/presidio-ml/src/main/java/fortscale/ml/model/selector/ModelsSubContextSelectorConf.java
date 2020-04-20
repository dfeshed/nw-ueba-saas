package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class ModelsSubContextSelectorConf implements IContextSelectorConf {
    public static final String MODELS_SUB_CONTEXT_SELECTOR = "models_sub_context_selector";
    public static final String MODEL_CONF_NAME_PROPERTY = "modelConfName";
    public static final String CONTEXT_FIELD_NAME_PROPERTY = "contextFieldName";


    private String modelConfName;
    private String contextFieldName;

    @JsonCreator
    public ModelsSubContextSelectorConf(
            @JsonProperty(MODEL_CONF_NAME_PROPERTY) String modelConfName,
            @JsonProperty(CONTEXT_FIELD_NAME_PROPERTY) String contextFieldName) {

        Assert.hasText(modelConfName, "modelConfName must contain text.");
        this.modelConfName = modelConfName;
        Assert.hasText(contextFieldName, "contextFieldName must contain text.");
        this.contextFieldName = contextFieldName;
    }

    @Override
    public String getFactoryName() {
        return MODELS_SUB_CONTEXT_SELECTOR;
    }

    public String getModelConfName() {
        return modelConfName;
    }

    public String getContextFieldName() {
        return contextFieldName;
    }
}
