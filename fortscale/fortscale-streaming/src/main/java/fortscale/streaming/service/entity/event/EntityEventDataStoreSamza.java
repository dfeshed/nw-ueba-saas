package fortscale.streaming.service.entity.event;

import fortscale.entity.event.EntityEventData;
import fortscale.entity.event.EntityEventDataMongoStore;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class EntityEventDataStoreSamza extends EntityEventDataMongoStore {
    private static Logger logger = LoggerFactory.getLogger(EntityEventDataStoreSamza.class);
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

    private List<EntityEventData> getMergedListFromMongoAndSamza(List<EntityEventData> entityEventDataListFromMongo, long modifiedAtEpochtimeLte) {
        List<EntityEventData> resList = new ArrayList<>(entityEventDataListFromMongo.size());

        for(EntityEventData entityEventData: entityEventDataListFromMongo) {
            EntityEventData entityEventData1FromSamzaStore = entityEventStore.get(getEntityEventDataKey(entityEventData));
            if(entityEventData1FromSamzaStore!=null) {
                if (entityEventData1FromSamzaStore.getModifiedAtEpochtime() <= modifiedAtEpochtimeLte) {
                    resList.add(entityEventData1FromSamzaStore);
                }
            } else {
                resList.add(entityEventData);
            }
        }

        return resList;
    }

    @Override
    public List<EntityEventData> getEntityEventDataWithModifiedAtEpochtimeLte(String entityEventName, long modifiedAtEpochtime) {
        List<EntityEventData> listFromMongo = super.getEntityEventDataWithModifiedAtEpochtimeLte(entityEventName, modifiedAtEpochtime);
        return getMergedListFromMongoAndSamza(listFromMongo, modifiedAtEpochtime);
    }

    @Override
    public List<EntityEventData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest){
        return super.getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(entityEventName,pageRequest);
    }

    @Override
    public List<EntityEventData> getEntityEventDataWithEndTimeInRange(String entityEventName, Date fromTime, Date toTime) {
        List<EntityEventData> returnedList = new ArrayList<>();
        for (EntityEventData fromMongo : super.getEntityEventDataWithEndTimeInRange(entityEventName, fromTime, toTime)) {
            EntityEventData fromSamza = entityEventStore.get(getEntityEventDataKey(fromMongo));
            if (fromSamza == null) {
                returnedList.add(fromMongo);
            } else {
                returnedList.add(fromSamza);
            }
        }

        return returnedList;
    }

    @Override
    public void storeEntityEventData(EntityEventData entityEventData) {
        if(entityEventData.getId()==null) { // First time
            super.storeEntityEventData(entityEventData);
            // Fetching the entity data from mongo so the id will be updated
            entityEventData = super.getEntityEventData(entityEventData.getEntityEventName(), entityEventData.getContextId(), entityEventData.getStartTime(), entityEventData.getEndTime());
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        } else if(entityEventData.isTransmitted()) { // Any update after the event is fired will be stored only in mongo
            // if this logging line is still here - blame Yoel
            logger.info("deleting " + getEntityEventDataKey(entityEventData));
            super.storeEntityEventData(entityEventData);
            entityEventStore.delete(getEntityEventDataKey(entityEventData));
        } else { // Updating
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        }
    }
}
