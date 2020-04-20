package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.util.AdeEnrichedRecordToAdeScoredEnrichedRecordResolver;

@Configuration
public class AdeEventTypeToAdeEnrichedRecordClassResolverConfig {
	@Bean
	public AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver() {
		return new AdeEventTypeToAdeEnrichedRecordClassResolver(this.getClass().getPackage().getName());
	}
}
