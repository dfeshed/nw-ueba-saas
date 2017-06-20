package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReaderFactoryService;

@Configuration
public class ScoringSpringConfiguration {
	@Autowired
	private ScorerConfService scorerConfService;
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;
	@Autowired
	private StatsService statsService;

	@Bean
	public ScorerConfService scorerConfService() {
		// TODO: Return a real ScorerConfService
		return null;
	}

	@Bean
	public FactoryService<Scorer> scorerFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public StatsService statsService() {
		// TODO: Return a real StatsService
		return null;
	}

	@Bean
	public FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService() {
		return new AdeRecordReaderFactoryService();
	}

	@Bean
	public ScoringService scoringService() {
		return new ScoringService(scorerConfService, scorerFactoryService, statsService);
	}
}
