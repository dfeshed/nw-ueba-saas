package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestDoubleMapRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestDoubleMapRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EpochtimeToHighestDoubleMapRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Override
    public String getFactoryName() {
        return EpochtimeToHighestDoubleMapRetrieverConf.EPOCHTIME_TO_HIGHEST_DOUBLE_MAP_RETRIEVER;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        EpochtimeToHighestDoubleMapRetrieverConf conf = (EpochtimeToHighestDoubleMapRetrieverConf)factoryConfig;
        return new EpochtimeToHighestDoubleMapRetriever(featureBucketReader, bucketConfigurationService, conf);
    }
}
