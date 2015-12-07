package fortscale.ml.model.builder;

public class ContinuousHistogramModelBuilderConf implements IModelBuilderConf {
	public static final String CONTINUOUS_HISTOGRAM_MODEL_BUILDER_CONF = "continuous_histogram_model_builder_conf";

	@Override
	public String getFactoryName() {
		return CONTINUOUS_HISTOGRAM_MODEL_BUILDER_CONF;
	}
}
