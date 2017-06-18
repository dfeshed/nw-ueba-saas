package fortscale.accumulator.entityEvent;


import fortscale.accumulator.accumulator.Accumulator;
import fortscale.accumulator.manager.AccumulatorManagerImpl;
import fortscale.entity.event.EntityEventConfService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class EntityEventAccumulatorManagerImpl extends AccumulatorManagerImpl {

    private final EntityEventConfService entityEventConfService;

    /**
     * C'tor
     *
     * @param accumulator accumulator to execute by
     */
    public EntityEventAccumulatorManagerImpl(Accumulator accumulator,EntityEventConfService entityEventConfService) {
        super(accumulator);
        this.entityEventConfService = entityEventConfService;
    }

    @Override
    protected Set<String> getFeatureNames() {
        return new HashSet<>(entityEventConfService.getEntityEventNames());
    }
}
