package fortscale.aggregation.feature.event.config;

import fortscale.aggregation.feature.event.RetentionStrategiesConfService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
public class RetentionStrategiesConfServiceConfig {
    @Bean
    public RetentionStrategiesConfService retentionStrategiesConfService()
    {
        return new RetentionStrategiesConfService();
    }
}
