package fortscale.acumulator.aggregation;

import fortscale.acumulator.AccumulationParams;
import fortscale.acumulator.Accumulator;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class AggregatedFeatureEventsAccumulator implements Accumulator {
    @Autowired
    AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;

    @Override
    public void run(AccumulationParams params) {

    }
}
