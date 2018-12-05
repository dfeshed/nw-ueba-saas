package presidio.output.processor.spring;

import fortscale.aggregation.feature.bucket.metrics.FeatureBucketAggregatorMetricsContainerConfig;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
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
import presidio.ade.sdk.common.AdeManagerSdkConfig;

@Configuration
@Import({
        MongoDbBulkOpUtilConfig.class,
        AggrDataToCollectionNameTranslatorConfig.class,
        AdeManagerSdkConfig.class
})
public class OutputAdeManagerSdkConfig {

    private final MongoTemplate mongoTemplate;
    private final AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Autowired
    public OutputAdeManagerSdkConfig(
            MongoTemplate mongoTemplate,
            AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator,
            MongoDbBulkOpUtil mongoDbBulkOpUtil) {

        this.mongoTemplate = mongoTemplate;
        this.aggrDataToCollectionNameTranslator = aggrDataToCollectionNameTranslator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Bean
    public ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader() {
        return new AggregatedDataStoreMongoImpl(
                mongoTemplate,
                aggrDataToCollectionNameTranslator,
                mongoDbBulkOpUtil);
    }
}
