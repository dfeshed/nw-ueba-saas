package presidio.ade.domain.record;

import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecordReaderFactory;

/**
 * This Spring configuration configures all the beans related to ADE record readers.
 * Specifically it configures the factory services and all the different factories.
 *
 * Created by Lior Govrin on 05/06/2017.
 */
@Configuration
public class AdeRecordReaderConfig {
	@Bean
	public FactoryService<RecordReader<EnrichedRecord>> enrichedRecordReaderFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public EnrichedRecordReaderFactory enrichedRecordReaderFactory() {
		return new EnrichedRecordReaderFactory();
	}
}
