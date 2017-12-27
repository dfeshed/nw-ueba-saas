package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilder;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import fortscale.ml.model.metrics.MaxContinuousModelBuilderMetricsContainer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by YaronDL on 9/24/2017.
 */
@SuppressWarnings("unused")
@Component
public class ContinuousMaxHistogramModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
    private ContinuousMaxHistogramModelBuilder continuousMaxHistogramModelBuilder;

    @Autowired
    private MaxContinuousModelBuilderMetricsContainer maxContinuousModelBuilderMetricsContainer;

    @Override
    public String getFactoryName() {
        return ContinuousMaxHistogramModelBuilderConf.CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER;
    }

    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {
        if (continuousMaxHistogramModelBuilder == null) {
            continuousMaxHistogramModelBuilder = new ContinuousMaxHistogramModelBuilder((ContinuousMaxHistogramModelBuilderConf)factoryConfig, maxContinuousModelBuilderMetricsContainer);
        }

        return continuousMaxHistogramModelBuilder;
    }
}