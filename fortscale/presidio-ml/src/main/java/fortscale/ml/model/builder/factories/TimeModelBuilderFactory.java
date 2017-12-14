package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.TimeModelBuilder;
import fortscale.ml.model.builder.TimeModelBuilderConf;
import fortscale.ml.model.metrics.TimeModeBuilderMetricsContainerConfig;
import fortscale.ml.model.metrics.TimeModelBuilderMetricsContainer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class TimeModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return TimeModelBuilderConf.TIME_MODEL_BUILDER;
	}

	@Autowired
	private TimeModelBuilderMetricsContainer timeModelBuilderMetricsContainer;

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		TimeModelBuilderConf config = (TimeModelBuilderConf)factoryConfig;
		return new TimeModelBuilder(config, timeModelBuilderMetricsContainer);
	}
}
