package fortscale.collection.jobs.model;

import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.accumulator.manager.AccumulatorManger;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

/**
 * work orderL
 * 1. runs accumulation
 * 2. build models
 * Created by barak_schuster on 10/30/16.
 */
public abstract class AccumulatedModelBuildingSyncService extends ModelBuildingSyncService{
    private final Set<String> featureNames;

    public AccumulatedModelBuildingSyncService(String sessionId, Collection<String> modelConfNames, long secondsBetweenEndTimes, long timeoutInSeconds, String controlInputTopic, String controlOutputTopic, Set<String> featureNames) {
        super(sessionId, modelConfNames, secondsBetweenEndTimes, timeoutInSeconds, controlInputTopic, controlOutputTopic);
        this.featureNames = featureNames;
    }

    @Override
    protected void sendCommands(long endTimeInSeconds)
    {
        accumulate(endTimeInSeconds);
        super.sendCommands(endTimeInSeconds);
    }

    private void accumulate(long endTimeInSeconds) {
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();
        accumulatorManagerParams.setFeatures(featureNames);
        accumulatorManagerParams.setTo(Instant.ofEpochSecond(endTimeInSeconds));
        getAccumulatorManger().run(accumulatorManagerParams);
    }

    protected abstract AccumulatorManger getAccumulatorManger();
}
