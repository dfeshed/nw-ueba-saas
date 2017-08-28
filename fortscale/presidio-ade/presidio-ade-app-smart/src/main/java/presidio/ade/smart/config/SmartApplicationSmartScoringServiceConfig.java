package presidio.ade.smart.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import fortscale.ml.scorer.records.AdeRecordReaderFactoriesConfig;
import fortscale.ml.scorer.records.RecordReaderFactoryServiceConfig;
import fortscale.ml.scorer.records.TransformationConfig;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.smart.SmartScoringService;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({AdeRecordReaderFactoriesConfig.class, TransformationConfig.class, RecordReaderFactoryServiceConfig.class})
public class SmartApplicationSmartScoringServiceConfig {
	@Value("${presidio.ade.scorer.base.configurations.path}")
	private String scorerBaseConfigurationsPath;
	@Value("${presidio.ade.scorer.overriding.configurations.path:#{null}}")
	private String scorerOverridingConfigurationsPath;
	@Value("${presidio.ade.scorer.additional.configurations.path:#{null}}")
	private String scorerAdditionalConfigurationsPath;

	@Autowired
	private RecordReaderFactoryService recordReaderFactoryService;
	@Autowired
	private ScorerConfService scorerConfService;
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;
	@Autowired
	private ScoringService scoringService;

	@Bean
	public ScorerConfService scorerConfService() {
		return new ScorerConfServiceImpl(
				scorerBaseConfigurationsPath,
				scorerOverridingConfigurationsPath,
				scorerAdditionalConfigurationsPath);
	}

	@Bean
	public FactoryService<Scorer> scorerFactoryService() {
		// TODO: Should register to factory service smart related scorer factories
		return new FactoryService<>();
	}

	@Bean
	public ScoringService scoringService() {
		return new ScoringService(scorerConfService, scorerFactoryService);
	}

	@Bean
	public SmartScoringService smartScoringService() {
		return new SmartScoringService(recordReaderFactoryService, scoringService);
	}
}
