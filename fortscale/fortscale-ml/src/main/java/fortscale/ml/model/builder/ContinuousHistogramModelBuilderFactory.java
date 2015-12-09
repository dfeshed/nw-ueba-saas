package fortscale.ml.model.builder;

import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContinuousHistogramModelBuilderFactory implements InitializingBean, Factory<IModelBuilder> {
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;

	private ContinuousHistogramModelBuilder continuousHistogramModelBuilder;

	@Override
	public void afterPropertiesSet() throws Exception {
		modelBuilderFactoryService.register(ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER, this);
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (continuousHistogramModelBuilder == null) {
			continuousHistogramModelBuilder = new ContinuousHistogramModelBuilder();
		}

		return continuousHistogramModelBuilder;
	}
}
