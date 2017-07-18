package fortscale.entity.event;

import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;

/**
 * Created by YaronDL on 7/17/2017.
 */
public class EntityEventBuilderFactory {
    private int retrievingPageSize;
    private int storePageSize;

    private String eventTypeFieldName;
    private String eventTypeFieldValue;
    private String entityEventTypeFieldName;
    private String epochtimeFieldName;

    private EntityEventDataStore entityEventDataStore;
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

    public EntityEventBuilderFactory(EntityEventDataStore entityEventDataStore, AggrFeatureEventBuilderService aggrFeatureEventBuilderService,
                                     int retrievingPageSize, int storePageSize, String eventTypeFieldName, String eventTypeFieldValue, String entityEventTypeFieldName, String epochtimeFieldName){
        this.entityEventDataStore = entityEventDataStore;
        this.aggrFeatureEventBuilderService = aggrFeatureEventBuilderService;
        this.retrievingPageSize = retrievingPageSize;
        this.storePageSize = storePageSize;
        this.eventTypeFieldName = eventTypeFieldName;
        this.eventTypeFieldValue = eventTypeFieldValue;
        this.entityEventTypeFieldName = entityEventTypeFieldName;
        this.epochtimeFieldName = epochtimeFieldName;
    }

    public EntityEventBuilder createEntityEventBuilder(long secondsToWaitBeforeFiring, EntityEventConf entityEventConf){
        return new EntityEventBuilder(secondsToWaitBeforeFiring, entityEventConf, entityEventDataStore, aggrFeatureEventBuilderService,
                retrievingPageSize, storePageSize, eventTypeFieldName, eventTypeFieldValue, entityEventTypeFieldName, epochtimeFieldName);
    }
}
