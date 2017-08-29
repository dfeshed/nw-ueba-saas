package presidio.ade.processes.shell.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/30/17.
 */

@Configuration
@Import(
//        application-specific confs
        ScorersFactoryConfig.class)
public class ScoringServiceConfig {
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Value("${fortscale.scorer.configurations.location.path}")
    private String scorerConfigurationsLocationPath;
    @Value("${fortscale.scorer.configurations.location.overriding.path:#{null}}")
    private String scorerConfigurationsOverridingPath=null;
    @Value("${fortscale.scorer.configurations.location.additional.path:#{null}}")
    private String scorerConfigurationsAdditionalPath=null;

    @Bean
    public ScorerConfService scorerConfService() {
        return new ScorerConfServiceImpl(scorerConfigurationsLocationPath,scorerConfigurationsOverridingPath,scorerConfigurationsAdditionalPath);
    }

    @Bean
    public ScoringService scoringService() {
        return new ScoringService(scorerConfService(), scorerFactoryService);
    }
}
