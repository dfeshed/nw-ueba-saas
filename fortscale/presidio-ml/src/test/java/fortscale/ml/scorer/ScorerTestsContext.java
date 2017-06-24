package fortscale.ml.scorer;

import fortscale.ml.scorer.record.JsonAdeRecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactory;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class ScorerTestsContext {
	@Autowired
	private Collection<RecordReaderFactory> recordReaderFactories;

	@Bean
	public RecordReaderFactoryService recordReaderFactoryService() {
		return new RecordReaderFactoryService(recordReaderFactories, Collections.emptySet());
	}

	@Bean
	public JsonAdeRecordReaderFactory jsonAdeRecordReaderFactory() {
		return new JsonAdeRecordReaderFactory();
	}
}
