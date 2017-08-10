package presidio.ade.domain.store.aggr;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.AdeEventTypeToAdeAggregationRecordClassResolver;
import presidio.ade.domain.record.aggregated.AdeEventTypeToAdeAggregationRecordClassResolverConfig;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
@Import({AggrDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class, AdeEventTypeToAdeAggregationRecordClassResolverConfig.class})
public class AggregatedDataStoreConfig {
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public AggrDataToCollectionNameTranslator translator;
    @Autowired
    public MongoDbBulkOpUtil mongoDbBulkOpUtil;
    @Autowired
    public AdeEventTypeToAdeAggregationRecordClassResolver adeEventTypeToAdeAggregationRecordClassResolver;
    @Bean
    public AggregatedDataStore aggrDataStore()
    {
        return new AggregatedDataStoreMongoImpl(mongoTemplate,translator,mongoDbBulkOpUtil,adeEventTypeToAdeAggregationRecordClassResolver );
    }

}
