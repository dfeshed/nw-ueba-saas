package fortscale.ml.scorer.spring.config;

import fortscale.ml.model.cache.EventModelsCacheServiceConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReaderFactoryService;

import java.util.List;

@Configuration
@Import({ScorersFactoriesConfig.class,EventModelsCacheServiceConfig.class,ScorerConfServiceConfig.class})
public class ScoringSpringConfiguration {
	@Autowired
	List<AbstractServiceAutowiringFactory<Scorer>> scorerFactory;

	@Autowired
	List<AbstractServiceAutowiringFactory<AbstractDataRetriever>> retrieverAbstractServiceAutowiringFactory;
	@Autowired
	private StatsService statsService;


	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Autowired
	private ScorerConfService scorerConfService;

	@Bean
	public FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService() {
		return new AdeRecordReaderFactoryService();
	}

	@Bean
	public ScoringService scoringService() {
		return new ScoringService(scorerConfService, scorerFactoryService, statsService);
	}
}
