package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.Model;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContinuousHistogramModelBuilder.class, name = ContinuousHistogramModelBuilder.MODEL_BUILDER_TYPE),
        @JsonSubTypes.Type(value = DiscreteModelBuilder.class, name = DiscreteModelBuilder.MODEL_BUILDER_TYPE)
})
public interface IModelBuilder {
        Model build(Object modelBuilderData);
}
