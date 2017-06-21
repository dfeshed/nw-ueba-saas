package fortscale.ml.scorer;

import fortscale.ml.scorer.record.JsonAdeRecordReaderFactory;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import presidio.ade.domain.record.AdeRecord;

@Configuration
@EnableSpringConfigured
public class ScorerTestsContext {
	@Bean
	public FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public Factory<RecordReader<AdeRecord>> recordReaderFactory() {
		return new JsonAdeRecordReaderFactory();
	}
}
