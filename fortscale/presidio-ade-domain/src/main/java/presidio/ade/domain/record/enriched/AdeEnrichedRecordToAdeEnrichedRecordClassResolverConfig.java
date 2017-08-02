package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

@Configuration
public class AdeEnrichedRecordToAdeEnrichedRecordClassResolverConfig {
	@Bean
	public AdeEnrichedRecordToAdeScoredEnrichedRecordResolver adeEventTypeToAdeEnrichedRecordClassResolver() {
		return new AdeEnrichedRecordToAdeScoredEnrichedRecordResolver(this.getClass().getPackage().getName());
	}
}
