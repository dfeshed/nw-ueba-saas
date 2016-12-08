package fortscale.collection.jobs.model;

import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;

/**
 * Created by barak_schuster on 10/30/16.
 */
public class EntityEventModelBuildingSyncService extends AccumulatedModelBuildingSyncService{

    @Autowired
    private EntityEventAccumulatorManagerImpl entityEventAccumulatorManager;

    public EntityEventModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic, Set<String> featureNames) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic, featureNames);
    }

    @Override
    protected AccumulatorManger getAccumulatorManger()
    {
        return entityEventAccumulatorManager;
    }
}
