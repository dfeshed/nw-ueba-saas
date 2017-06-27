package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("fortscale.ml.scorer.factory")
public class ScoringSpringConfiguration {
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;
	@Autowired
	private StatsService statsService;

	@Value("${fortscale.scorer.configurations.location.path}")
	private String scorerConfigurationsLocationPath;
	@Value("${fortscale.scorer.configurations.location.overriding.path:#{null}}")
	private String scorerConfigurationsOverridingPath=null;
	@Value("${fortscale.scorer.configurations.location.additional.path:#{null}}")
	private String scorerConfigurationsAdditionalPath=null;

	@Bean
	public ScorerConfService scorerConfService() {
		// TODO: Return a real ScorerConfService
		return new ScorerConfServiceImpl(scorerConfigurationsLocationPath,scorerConfigurationsOverridingPath,scorerConfigurationsAdditionalPath);
	}

	@Bean
	public FactoryService<Scorer> scorerFactoryService() {
		return new FactoryService<>();
	}


	@Bean
	public ScoringService scoringService() {
		return new ScoringService(scorerConfService(), scorerFactoryService, statsService);
	}
}
