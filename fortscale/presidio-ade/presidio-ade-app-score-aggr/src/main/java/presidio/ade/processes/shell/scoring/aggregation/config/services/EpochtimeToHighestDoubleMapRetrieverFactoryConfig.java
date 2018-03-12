package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestDoubleMapRetriever;
import fortscale.ml.model.retriever.EpochtimeToHighestDoubleMapRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class EpochtimeToHighestDoubleMapRetrieverFactoryConfig {
    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;

    @Bean
    public AbstractServiceAutowiringFactory<AbstractDataRetriever> epochtimeToHighestDoubleMapRetrieverFactory() {
        return new AbstractServiceAutowiringFactory<AbstractDataRetriever>() {
            @Override
            public String getFactoryName() {
                return EpochtimeToHighestDoubleMapRetrieverConf.EPOCHTIME_TO_HIGHEST_DOUBLE_MAP_RETRIEVER;
            }

            @Override
            public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
                EpochtimeToHighestDoubleMapRetrieverConf conf = (EpochtimeToHighestDoubleMapRetrieverConf)factoryConfig;
                return new EpochtimeToHighestDoubleMapRetriever(featureBucketReader, bucketConfigurationService, conf);
            }
        };
    }
}
