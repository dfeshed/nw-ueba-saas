package presidio.ade.domain.store.enriched;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Import({
		EnrichedDataToCollectionNameTranslatorConfig.class
})
public class EnrichedDataStoreConfig {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private EnrichedDataToCollectionNameTranslator translator;

	@Bean
	public EnrichedDataStore enrichedDataStore() {
		return new EnrichedDataStoreImplMongo(mongoTemplate, translator);
	}
}
