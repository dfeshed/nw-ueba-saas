package presidio.ade.domain.record.enriched;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.util.DataSourceToAdeRecordClassResolver;

@Configuration
public class DataSourceToAdeEnrichedRecordClassResolverConfig {
	@Bean
	public DataSourceToAdeEnrichedRecordClassResolver dataSourceToAdeEnrichedRecordClassResolver() {
		return new DataSourceToAdeEnrichedRecordClassResolver(this.getClass().getPackage().getName());
	}
}
