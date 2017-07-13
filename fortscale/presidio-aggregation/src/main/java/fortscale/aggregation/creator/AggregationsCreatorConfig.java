package fortscale.aggregation.creator;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.config.AggregatedFeatureEventsConfServiceConfig;
import fortscale.aggregation.feature.functions.AggrFeatureFuncServiceConfig;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 7/10/17.
 */
@Configuration
@Import({AggregatedFeatureEventsConfServiceConfig.class,AggrFeatureFuncServiceConfig.class})
public class AggregationsCreatorConfig {

    @Autowired
    private IAggrFeatureEventFunctionsService aggrFeatureEventFunctionsService;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Bean
    public AggregationsCreator aggregationsCreator()
    {
        return new AggregationsCreatorImpl(aggrFeatureEventFunctionsService,aggregatedFeatureEventsConfService);
    }
}
