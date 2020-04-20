package fortscale.aggregation.feature.functions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
public class AggrFeatureFuncServiceConfig {
    @Bean
    public AggrFeatureFuncService aggrFeatureFuncService()
    {
        return new AggrFeatureFuncService();
    }
}
