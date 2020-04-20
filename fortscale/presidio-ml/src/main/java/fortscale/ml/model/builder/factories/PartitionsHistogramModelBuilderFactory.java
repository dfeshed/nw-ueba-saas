package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.PartitionsHistogramModelBuilder;
import fortscale.ml.model.builder.PartitionsHistogramModelBuilderConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;


@SuppressWarnings("unused")
@Component
public class PartitionsHistogramModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
    private PartitionsHistogramModelBuilder partitionsHistogramModelBuilder;

    @Override
    public String getFactoryName() {
        return PartitionsHistogramModelBuilderConf.INSTANT_TO_VALUE_HISTOGRAM_MODEL_BUILDER;
    }

    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {
        if (partitionsHistogramModelBuilder == null) {
            partitionsHistogramModelBuilder = new PartitionsHistogramModelBuilder((PartitionsHistogramModelBuilderConf) factoryConfig);
        }

        return partitionsHistogramModelBuilder;
    }
}