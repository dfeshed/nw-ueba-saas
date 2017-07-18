package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;

/**
 * Created by YaronDL on 7/17/2017.
 */
public class EntityEventServiceFactory {
    private EntityEventConfService entityEventConfService;
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    private EntityEventBuilderFactory entityEventBuilderFactory;

    public EntityEventServiceFactory(EntityEventConfService entityEventConfService,
                                     AggrFeatureEventBuilderService aggrFeatureEventBuilderService,
                                     EntityEventBuilderFactory entityEventBuilderFactory) {
        this.entityEventConfService = entityEventConfService;
        this.aggrFeatureEventBuilderService = aggrFeatureEventBuilderService;
        this.entityEventBuilderFactory = entityEventBuilderFactory;
    }

    public EntityEventService createEntityEventService(){
        return new EntityEventService(entityEventConfService, aggrFeatureEventBuilderService, entityEventBuilderFactory);
    }
}
