package fortscale.collection.jobs.model;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by barak_schuster on 10/30/16.
 */
public class AggregatedEventsModelBuildingSyncService extends ModelBuildingSyncService {
    @Autowired
    private AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager;

    public AggregatedEventsModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic);
    }

    protected AccumulatorManger getAccumulatorManger()
    {
        return aggregatedFeatureEventsAccumulatorManager;
    }
}
