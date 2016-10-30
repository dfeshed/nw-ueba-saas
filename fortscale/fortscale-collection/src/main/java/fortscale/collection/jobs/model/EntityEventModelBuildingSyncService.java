package fortscale.collection.jobs.model;

import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by barak_schuster on 10/30/16.
 */
public class EntityEventModelBuildingSyncService extends ModelBuildingSyncService{

    @Autowired
    private EntityEventAccumulatorManagerImpl entityEventAccumulatorManager;

    public EntityEventModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic);
    }

    @Override
    protected AccumulatorManger getAccumulatorManger()
    {
        return entityEventAccumulatorManager;
    }
}
