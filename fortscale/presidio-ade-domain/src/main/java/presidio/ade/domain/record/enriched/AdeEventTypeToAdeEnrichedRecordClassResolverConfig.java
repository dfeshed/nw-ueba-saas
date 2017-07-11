package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdeEventTypeToAdeEnrichedRecordClassResolverConfig {
	@Bean
	public AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver() {
		return new AdeEventTypeToAdeEnrichedRecordClassResolver(this.getClass().getPackage().getName());
	}
}
