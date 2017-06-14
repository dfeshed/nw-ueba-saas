package presidio.ade.domain.record.scanning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdeRecordTypeToClassConfig {
	@Bean
	public AdeRecordTypeToClass adeRecordTypeToClass() {
		return new AdeRecordTypeToClass("presidio.ade.domain");
	}
}
