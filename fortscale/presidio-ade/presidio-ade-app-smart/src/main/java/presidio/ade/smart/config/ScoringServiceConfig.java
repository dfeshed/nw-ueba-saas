package presidio.ade.smart.config;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScoringService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.metrics.ScoringServiceMetricsContainer;
import fortscale.ml.scorer.metrics.ScoringServiceMetricsContainerConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by maria_dorohin on 10/26/17.
 */
@Configuration
@Import({ScoringServiceMetricsContainerConfig.class
})
public class ScoringServiceConfig {

    @Autowired
    private ScorerConfService scorerConfService;
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private ModelsCacheService modelCacheService;
    @Autowired
    private ScoringServiceMetricsContainer scoringServiceMetricsContainer;

    @Bean
    public ScoringService scoringService() {
        return new ScoringService(scorerConfService, scorerFactoryService, modelCacheService, scoringServiceMetricsContainer);
    }
}
