package fortscale.streaming.service.entity.event;

import fortscale.entity.event.EntityEventData;
import fortscale.entity.event.EntityEventDataMongoStore;
import fortscale.entity.event.EntityEventMetaData;
import fortscale.streaming.ExtendedSamzaTaskContext;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;

@Configurable(preConstruction=true)
public class EntityEventDataStoreSamza extends EntityEventDataMongoStore {
    private static final String STORE_NAME_PROPERTY = "fortscale.entity.events.store.name";
    private static final String DELIMITER = "_";

    private KeyValueStore<String, EntityEventData> entityEventStore;

    @Value("${fortscale.entity.event.store.page.size}")
    private int storePageSize;

    @Autowired
    private EntityEventMetaDataMongoStore entityEventMetaDataMongoStore;

    @SuppressWarnings("unchecked")
    public EntityEventDataStoreSamza(ExtendedSamzaTaskContext context) {
        Assert.notNull(context);
        Config config = context.getConfig();
        String storeName = getConfigString(config, STORE_NAME_PROPERTY);
        entityEventStore = (KeyValueStore<String, EntityEventData>)context.getStore(storeName);
        Assert.notNull(entityEventStore);
    }

    public void sync(){
        List<EntityEventData> entityEventDataList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        long transmitionTime = System.currentTimeMillis()/1000;
        KeyValueIterator<String, EntityEventData> iter = entityEventStore.all();
        try {
            while (iter.hasNext()) {
                Entry<String, EntityEventData> entry = iter.next();
                keyList.add(entry.getKey());
                EntityEventData entityEventData = entry.getValue();
                entityEventData.setTransmitted(true);
                entityEventData.setTransmissionEpochtime(transmitionTime);
                entityEventDataList.add(entityEventData);
                if(entityEventDataList.size() >= storePageSize){
                    super.storeEntityEventDataList(entityEventDataList);
                    entityEventDataList = new ArrayList<>();
                }
            }
            if(entityEventDataList.size() > 0) {
                super.storeEntityEventDataList(entityEventDataList);
            }

        } finally {
            if (iter != null) {
                iter.close();
            }
        }

        for(String key: keyList){
            entityEventStore.delete(key);
        }
        entityEventMetaDataMongoStore.dropAll();
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
    public List<EntityEventMetaData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest){
        return entityEventMetaDataMongoStore.getEntityEventMetaData(entityEventName,pageRequest);
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
        EntityEventData prevEntityEventData = entityEventStore.get(getEntityEventDataKey(entityEventData));
        if(prevEntityEventData == null) { // First time
            entityEventMetaDataMongoStore.storeEntityEventMetaData(new EntityEventMetaData(entityEventData));
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        } else if(entityEventData.isTransmitted()) { // Any update after the event is fired will be stored only in mongo
            super.storeEntityEventData(entityEventData);
            entityEventMetaDataMongoStore.removeEntityEventMetaData(entityEventData.getEntityEventName(),entityEventData.getContextId(), entityEventData.getStartTime());
            entityEventStore.delete(getEntityEventDataKey(entityEventData));
        } else { // Updating
            entityEventStore.put(getEntityEventDataKey(entityEventData), entityEventData);
        }
    }


    @Override
    //Assumption all events here are after transmition!!!
    public void storeEntityEventDataList(List<EntityEventData> entityEventDataList) {
        super.storeEntityEventDataList(entityEventDataList);
        for(EntityEventData entityEventData: entityEventDataList){
            entityEventMetaDataMongoStore.removeEntityEventMetaData(entityEventData.getEntityEventName(),entityEventData.getContextId(), entityEventData.getStartTime());
            entityEventStore.delete(getEntityEventDataKey(entityEventData));
        }
    }
}
