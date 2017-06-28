package presidio.ade.domain.store.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnrichedDataToCollectionNameTranslatorConfig {
	@Bean
	public EnrichedDataAdeToCollectionNameTranslator enrichedDataToCollectionNameTranslator() {
		return new EnrichedDataAdeToCollectionNameTranslator();
	}
}
