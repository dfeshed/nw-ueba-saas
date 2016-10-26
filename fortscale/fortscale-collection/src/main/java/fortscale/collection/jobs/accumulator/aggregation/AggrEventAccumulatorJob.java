package fortscale.collection.jobs.accumulator.aggregation;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.collection.jobs.accumulator.BaseAccumulatorJob;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by barak_schuster on 10/18/16.
 */
public class AggrEventAccumulatorJob extends BaseAccumulatorJob {

    @Autowired
    private AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager;

    @Override
    public void runAccumulation() {
        aggregatedFeatureEventsAccumulatorManager.run(accumulatorManagerParams);
    }

}
