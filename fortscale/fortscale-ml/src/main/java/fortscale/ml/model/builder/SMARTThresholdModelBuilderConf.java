package fortscale.ml.model.builder;

public class SMARTThresholdModelBuilderConf implements IModelBuilderConf {
	public static final String SMART_THRESHOLD_MODEL_BUILDER = "smart_threshold_model_builder";

	@Override
	public String getFactoryName() {
		return SMART_THRESHOLD_MODEL_BUILDER;
	}
}
