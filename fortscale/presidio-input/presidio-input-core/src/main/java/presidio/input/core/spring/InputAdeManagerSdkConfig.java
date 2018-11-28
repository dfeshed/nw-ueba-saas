package presidio.input.core.spring;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainer;
import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainerConfig;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.ScoredDataReader;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslatorConfig;
import presidio.ade.domain.store.aggr.AggregatedDataStoreMongoImpl;

@Configuration
@Import({
        FeatureBucketAggregatorMetricsContainerConfig.class,
        MongoDbBulkOpUtilConfig.class,
        AggrDataToCollectionNameTranslatorConfig.class
})
public class InputAdeManagerSdkConfig {
    private final BucketConfigurationService bucketConfigurationService;
    private final RecordReaderFactoryService recordReaderFactoryService;
    private final FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer;
    private final MongoTemplate mongoTemplate;
    private final AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Autowired
    public InputAdeManagerSdkConfig(
            BucketConfigurationService bucketConfigurationService,
            RecordReaderFactoryService recordReaderFactoryService,
            FeatureBucketAggregatorMetricsContainer featureBucketAggregatorMetricsContainer,
            MongoTemplate mongoTemplate,
            AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator,
            MongoDbBulkOpUtil mongoDbBulkOpUtil) {

        this.bucketConfigurationService = bucketConfigurationService;
        this.recordReaderFactoryService = recordReaderFactoryService;
        this.featureBucketAggregatorMetricsContainer = featureBucketAggregatorMetricsContainer;
        this.mongoTemplate = mongoTemplate;
        this.aggrDataToCollectionNameTranslator = aggrDataToCollectionNameTranslator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Bean
    public InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator() {
        return new InMemoryFeatureBucketAggregator(
                bucketConfigurationService,
                recordReaderFactoryService,
                featureBucketAggregatorMetricsContainer);
    }

    @Bean
    public ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader() {
        return new AggregatedDataStoreMongoImpl(
                mongoTemplate,
                aggrDataToCollectionNameTranslator,
                mongoDbBulkOpUtil);
    }
}
