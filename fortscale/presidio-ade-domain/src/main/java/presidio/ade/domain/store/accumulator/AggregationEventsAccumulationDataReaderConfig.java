package presidio.ade.domain.store.accumulator;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
@Import({AccumulatedDataToCollectionNameTranslatorConfig.class, MongoDbBulkOpUtilConfig.class})
public class AggregationEventsAccumulationDataReaderConfig {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AccumulatedDataToCollectionNameTranslator translator;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader() {
        return new AggregationEventsAccumulationDataStoreMongoImpl(mongoTemplate, translator, mongoDbBulkOpUtil);
    }
}
