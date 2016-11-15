package fortscale.collection.jobs.accumulator.aggregation.config;

import fortscale.accumulator.aggregation.config.AggregatedFeatureEventsAccumulatorManagerImplConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/16/16.
 */
@Configuration
@Import({AggregatedFeatureEventsAccumulatorManagerImplConfig.class})
public class AggrEventAccumulatorJobConfig {
}
