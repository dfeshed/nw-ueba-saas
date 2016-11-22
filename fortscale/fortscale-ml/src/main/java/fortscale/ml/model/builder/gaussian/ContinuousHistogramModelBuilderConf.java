package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.IModelBuilderConf;

public class ContinuousHistogramModelBuilderConf implements IModelBuilderConf {
	public static final String CONTINUOUS_HISTOGRAM_MODEL_BUILDER = "continuous_histogram_model_builder";

	@Override
	public String getFactoryName() {
		return CONTINUOUS_HISTOGRAM_MODEL_BUILDER;
	}
}
