package presidio.ade.domain.store.enriched;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolverConfig;

@Configuration
@Import({
        EnrichedDataToCollectionNameTranslatorConfig.class,
        AdeEventTypeToAdeEnrichedRecordClassResolverConfig.class,
        MongoDbBulkOpUtilConfig.class
})
public class EnrichedDataStoreConfig {
    @Value("${enriched.data.store.context.id.to.num.of.items.page.size:50000}")
    private long contextIdToNumOfItemsPageSize;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private EnrichedDataAdeToCollectionNameTranslator translator;
    @Autowired
    private AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Bean
    public StoreManagerAwareEnrichedDataStore enrichedDataStore() {
        return new EnrichedDataStoreImplMongo(
                mongoTemplate,
                translator,
                adeEventTypeToAdeEnrichedRecordClassResolver,
                mongoDbBulkOpUtil,
                contextIdToNumOfItemsPageSize);
    }
}
