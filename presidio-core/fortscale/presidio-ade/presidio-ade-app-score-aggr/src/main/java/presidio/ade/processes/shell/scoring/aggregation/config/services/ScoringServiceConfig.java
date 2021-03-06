package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainer;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainerConfig;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import fortscale.ml.scorer.metrics.ScoringServiceMetricsContainer;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({ScoringServiceMetricsContainer.class,
        ModelCacheMetricsContainerConfig.class,
//        application-specific confs
        ScorersFactoryConfig.class})
public class ScoringServiceConfig {
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Value("${fortscale.scorer.configurations.location.path}")
    private String scorerConfigurationsLocationPath;
    @Value("${fortscale.scorer.configurations.location.overriding.path:#{null}}")
    private String scorerConfigurationsOverridingPath=null;
    @Value("${fortscale.scorer.configurations.location.additional.path:#{null}}")
    private String scorerConfigurationsAdditionalPath=null;
    @Autowired
    private ModelsCacheService modelCacheService;

    @Autowired
    private ScoringServiceMetricsContainer scoringServiceMetricsContainer;
    @Autowired
    private ModelCacheMetricsContainer modelCacheMetricsContainer;
    @Bean
    public ScorerConfService scorerConfService() {
        // TODO: Return a real ScorerConfService
        return new ScorerConfServiceImpl(scorerConfigurationsLocationPath,scorerConfigurationsOverridingPath,scorerConfigurationsAdditionalPath);
    }

    @Bean
    public ScoringService scoringService() {
        return new ScoringService(scorerConfService(), scorerFactoryService, modelCacheService, scoringServiceMetricsContainer, modelCacheMetricsContainer);
    }
}

