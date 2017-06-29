package presidio.ade.domain.store.enriched;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.enriched.DataSourceToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.DataSourceToAdeEnrichedRecordClassResolverConfig;

@Configuration
@Import({
		EnrichedDataToCollectionNameTranslatorConfig.class,
		DataSourceToAdeEnrichedRecordClassResolverConfig.class
})
public class EnrichedDataStoreConfig {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private EnrichedDataAdeToCollectionNameTranslator translator;
	@Autowired
	private DataSourceToAdeEnrichedRecordClassResolver dataSourceToAdeEnrichedRecordClassResolver;

	@Bean
	public EnrichedDataStore enrichedDataStore() {
		return new EnrichedDataStoreImplMongo(mongoTemplate, translator, dataSourceToAdeEnrichedRecordClassResolver);
	}
}
