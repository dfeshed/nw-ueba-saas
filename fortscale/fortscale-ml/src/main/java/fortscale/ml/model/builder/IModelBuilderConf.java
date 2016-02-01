package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousHistogramModelBuilderConf.class, name = ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER),
		@JsonSubTypes.Type(value = DiscreteModelBuilderConf.class, name = DiscreteModelBuilderConf.DISCRETE_MODEL_BUILDER),
		@JsonSubTypes.Type(value = TimeModelBuilderConf.class, name = TimeModelBuilderConf.TIME_MODEL_BUILDER)
})
public interface IModelBuilderConf extends FactoryConfig {}
