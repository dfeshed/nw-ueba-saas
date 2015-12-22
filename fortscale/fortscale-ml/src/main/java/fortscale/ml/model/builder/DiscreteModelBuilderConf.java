package fortscale.ml.model.builder;

public class DiscreteModelBuilderConf implements IModelBuilderConf {
	public static final String DISCRETE_MODEL_BUILDER = "discrete_model_builder";

	@Override
	public String getFactoryName() {
		return DISCRETE_MODEL_BUILDER;
	}
}
