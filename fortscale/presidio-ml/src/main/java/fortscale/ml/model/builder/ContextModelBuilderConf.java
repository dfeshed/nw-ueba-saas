package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;


@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class ContextModelBuilderConf implements IModelBuilderConf {
    public static final String CONTEXT_MODEL_BUILDER = "context_model_builder";

    @Override
    public String getFactoryName() {
        return CONTEXT_MODEL_BUILDER;
    }

}
