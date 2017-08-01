package presidio.ade.domain.store.accumulator;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslatorConfig;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreMongoImpl;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
@Import({AccumulatedDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class})
public class AccumulatedDataStoreConfig {
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    public AccumulatedDataToCollectionNameTranslator translator;
    @Autowired
    public MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public AccumulatedDataStore aggrDataStore()
    {
        return new AccumulatedDataStoreMongoImpl(mongoTemplate,translator,mongoDbBulkOpUtil);
    }

}
