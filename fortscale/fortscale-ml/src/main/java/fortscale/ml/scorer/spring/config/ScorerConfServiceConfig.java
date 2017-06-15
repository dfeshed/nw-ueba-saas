package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerConfServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/15/17.
 */
@Configuration
public class ScorerConfServiceConfig {
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

}
