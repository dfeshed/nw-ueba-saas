package fortscale.collection.jobs.accumulator;

import fortscale.accumulator.aggregation.config.AggregatedFeatureEventsAccumulatorManagerImplConfig;
import fortscale.accumulator.entityEvent.config.EntityEventAccumulatorManagerImplConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 10/16/16.
 */
@Configuration
@Import({AggregatedFeatureEventsAccumulatorManagerImplConfig.class, EntityEventAccumulatorManagerImplConfig.class})
public class AccumulatorJobConfig {
}
