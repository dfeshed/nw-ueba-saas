package fortscale.collection.jobs.accumulator.entity.event;

import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.collection.jobs.accumulator.BaseAccumulatorJob;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * accumulates entity events
 *
 * Created by barak_schuster on 10/18/16.
 */
public class EntityEventAccumulatorJob extends BaseAccumulatorJob {
    @Autowired
    private EntityEventAccumulatorManagerImpl entityEventAccumulatorManager;

    @Override
    public void runAccumulation() {
        entityEventAccumulatorManager.run(accumulatorManagerParams);
    }

}
