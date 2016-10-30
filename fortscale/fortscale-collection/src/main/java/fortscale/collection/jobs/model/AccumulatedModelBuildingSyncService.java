package fortscale.collection.jobs.model;

import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.accumulator.manager.AccumulatorManger;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * work orderL
 * 1. runs accumulation
 * 2. build models
 * Created by barak_schuster on 10/30/16.
 */
public abstract class AccumulatedModelBuildingSyncService extends ModelBuildingSyncService{
    public AccumulatedModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic);
    }

    @Override
    protected void sendCommands(long endTimeInSeconds)
    {
        accumulate(endTimeInSeconds);
        super.sendCommands(endTimeInSeconds);
    }

    private void accumulate(long endTimeInSeconds) {
        Set<String> features = new HashSet<>(modelConfNames);
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();
        accumulatorManagerParams.setFeatures(features);
        accumulatorManagerParams.setTo(Instant.ofEpochSecond(endTimeInSeconds));
        getAccumulatorManger().run(accumulatorManagerParams);
    }

    protected abstract AccumulatorManger getAccumulatorManger();
}
