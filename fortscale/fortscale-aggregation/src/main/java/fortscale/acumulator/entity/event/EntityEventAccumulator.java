package fortscale.acumulator.entity.event;

import fortscale.acumulator.AccumulationParams;
import fortscale.acumulator.Accumulator;
import fortscale.entity.event.EntityEventMongoStore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by barak_schuster on 10/6/16.
 */
public class EntityEventAccumulator implements Accumulator {
    @Autowired
    EntityEventMongoStore entityEventMongoStore;

    @Override
    public void run(AccumulationParams params) {

    }
}
