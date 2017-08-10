package presidio.ade.domain.store.scored.feature_aggregation;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.AdeEventTypeToAdeAggregationRecordClassResolver;
import presidio.ade.domain.record.aggregated.AdeEventTypeToAdeAggregationRecordClassResolverConfig;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslatorConfig;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreMongoImpl;

/**
 * Created by YaronDL on 6/13/2017.
 */

@Configuration
@Import({AggrDataToCollectionNameTranslatorConfig.class,
         MongoDbBulkOpUtilConfig.class,AdeEventTypeToAdeAggregationRecordClassResolverConfig.class
})
public class ScoredFeatureAggregationDataStoreConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AggrDataToCollectionNameTranslator translator;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;
    @Autowired
    private AdeEventTypeToAdeAggregationRecordClassResolver adeEventTypeToAdeAggregationRecordClassResolver;

    @Bean
    public AggregatedDataStore aggregatedDataStore() {
        return new AggregatedDataStoreMongoImpl(mongoTemplate, translator, mongoDbBulkOpUtil,adeEventTypeToAdeAggregationRecordClassResolver );
    }
}
