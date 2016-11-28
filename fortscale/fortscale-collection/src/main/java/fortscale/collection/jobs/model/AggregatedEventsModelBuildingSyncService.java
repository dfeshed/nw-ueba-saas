package fortscale.collection.jobs.model;

import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;

/**
 * Created by barak_schuster on 10/30/16.
 */
public class AggregatedEventsModelBuildingSyncService extends AccumulatedModelBuildingSyncService {
    @Autowired
    private AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager;

    public AggregatedEventsModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic, Set<String> featureNames) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic, featureNames);
    }

    protected AccumulatorManger getAccumulatorManger()
    {
        return aggregatedFeatureEventsAccumulatorManager;
    }
}
