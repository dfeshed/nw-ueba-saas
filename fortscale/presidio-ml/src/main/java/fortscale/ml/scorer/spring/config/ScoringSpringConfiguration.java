package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReaderFactoryService;

@Configuration
public class ScoringSpringConfiguration {

	@Autowired
	private StatsService statsService;

	@Value("${fortscale.scorer.configurations.location.path}")
	private String scorerConfigurationsLocationPath;
	@Value("${fortscale.scorer.configurations.location.overriding.path:}")
	private String scorerConfigurationsOverridingPath;
	@Value("${fortscale.scorer.configurations.location.additional.path:}")
	private String scorerConfigurationsAdditionalPath;

	@Bean
	public ScorerConfService scorerConfService() {
		// TODO: Return a real ScorerConfService
		return new ScorerConfServiceImpl(scorerConfigurationsLocationPath,scorerConfigurationsOverridingPath.isEmpty()?null:scorerConfigurationsOverridingPath,scorerConfigurationsAdditionalPath.isEmpty()?null:scorerConfigurationsAdditionalPath);
	}

	@Bean
	public FactoryService<Scorer> scorerFactoryService() {
		return new FactoryService<>();
	}

	@Bean
	public FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService() {
		return new AdeRecordReaderFactoryService();
	}

	@Bean
	public ScoringService scoringService() {
		return new ScoringService(scorerConfService(), scorerFactoryService(), statsService);
	}
}
