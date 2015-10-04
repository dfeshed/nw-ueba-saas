package fortscale.streaming.service.aggregation.entity.event;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class EntityEventDataStoreSamza extends EntityEventDataMongoStore {
    private static final String STORE_NAME_PROPERTY = "fortscale.entity.events.store.name";
    private static final String DELIMITER = "_";

    private KeyValueStore<String, EntityEventData> entityEventStore;

    @SuppressWarnings("unchecked")
    public EntityEventDataStoreSamza(ExtendedSamzaTaskContext context) {
        Assert.notNull(context);
        Config config = context.getConfig();
        String storeName = getConfigString(config, STORE_NAME_PROPERTY);
        entityEventStore = (KeyValueStore<String, EntityEventData>)context.getStore(storeName);
        Assert.notNull(entityEventStore);
    }

    public String getEntityEventDataKey(String entityEventName, String contextId, long startTime, long endTime) {
        return entityEventName + DELIMITER +  contextId + DELIMITER + startTime +DELIMITER + endTime;
    }

    public String getEntityEventDataKey(EntityEventData entityEventData) {
        return getEntityEventDataKey(entityEventData.getEntityEventName(), entityEventData.getContextId(), entityEventData.getStartTime(), entityEventData.getEndTime());
    }

    @Override
    public EntityEventData getEntityEventData(String entityEventName, String contextId, long startTime, long endTime) {
        EntityEventData res = entityEventStore.get(getEntityEventDataKey(entityEventName, contextId, startTime, endTime));
        if(res==null) {
            res = super.getEntityEventData(entityEventName, contextId, startTime, endTime);
        }
        return res;
    }

    @Override
    public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
        return super.getEntityEventDataWithModifiedAtEpochtimeLte(entityEventName, modifiedAtEpochtime);
    }

    @Override
    public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(String entityEventName, long modifiedAtEpochtime) {
        return super.getEntityEventDataWithModifiedAtEpochtimeLteThatWereNotTransmitted(entityEventName, modifiedAtEpochtime);
    }

    @Override
    public void storeEntityEventData(EntityEventData entityEventData) {
        if(entityEventData.getId()==null) { // First time
            super.storeEntityEventData(entityEventData);
            // Fetching the entity data from mongo so the id will be updated
            entityEventData = super.getEntityEventData(entityEventData.getEntityEventName(), entityEventData.getContextId(), entityEventData.getStartTime(), entityEventData.getEndTime());
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        } else if(entityEventData.isTransmitted()) { // Any update after the event is fired will be stored only in mongo
            super.storeEntityEventData(entityEventData);
            entityEventStore.delete(getEntityEventDataKey(entityEventData));
        } else { // Updating
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        }
    }
}
