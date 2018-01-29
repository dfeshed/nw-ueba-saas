package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestIntegerMapRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestIntegerMapRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EpochtimeToHighestIntegerMapRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Override
    public String getFactoryName() {
        return EpochtimeToHighestIntegerMapRetrieverConf.EPOCHTIME_TO_HIGHEST_INTEGER_MAP_RETRIEVER;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        EpochtimeToHighestIntegerMapRetrieverConf conf = (EpochtimeToHighestIntegerMapRetrieverConf)factoryConfig;
        return new EpochtimeToHighestIntegerMapRetriever(featureBucketReader, bucketConfigurationService, conf);
    }
}
