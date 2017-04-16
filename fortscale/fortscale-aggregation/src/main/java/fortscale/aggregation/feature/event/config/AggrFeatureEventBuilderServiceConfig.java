package fortscale.aggregation.feature.event.config;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 4/16/17.
 */
@Configuration
@Import({AggregatedFeatureEventsConfUtilServiceConfig.class})
public class AggrFeatureEventBuilderServiceConfig {
    @Bean
    public AggrFeatureEventBuilderService aggrFeatureEventBuilderService()
    {
        return new AggrFeatureEventBuilderService();
    }
}
