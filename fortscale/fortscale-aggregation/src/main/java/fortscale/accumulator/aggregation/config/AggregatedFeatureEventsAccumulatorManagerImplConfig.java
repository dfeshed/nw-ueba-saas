package fortscale.accumulator.aggregation.config;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulator;
import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/9/16.
 */
@Configuration
@Import(AggregatedFeatureEventsAccumulatorConfig.class)
public class AggregatedFeatureEventsAccumulatorManagerImplConfig {
    @Autowired
    AggregatedFeatureEventsAccumulator accumulator;
    @Autowired
    AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Bean
    public AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager() {
        return new AggregatedFeatureEventsAccumulatorManagerImpl(accumulator, aggregatedFeatureEventsConfService);
    }

}
