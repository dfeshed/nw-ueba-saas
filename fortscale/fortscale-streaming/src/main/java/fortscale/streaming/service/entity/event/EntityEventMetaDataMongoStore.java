package fortscale.streaming.service.entity.event;


import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.entity.event.EntityEventData;
import fortscale.entity.event.EntityEventMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventMetaDataMongoStore {
    private static final String COLLECTION_NAME_PREFIX = "entity_event_meta_data_";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbUtilService mongoDbUtilService;


    public void removeAllTransmitted(){
        Query query = new Query();
        query.addCriteria(where(EntityEventMetaData.TRANSMITTED_FIELD).is(true));
        for(String collectionName: mongoDbUtilService.getCollections()){
            if(collectionName.startsWith(COLLECTION_NAME_PREFIX)){
                mongoTemplate.remove(query,collectionName);
            }
        }
    }


    public void updateEntityEventMetaDataAsTransmitted(String entityEventName, String contextId, long startTime, long transmissionEpochtime) {
        String collectionName = getCollectionName(entityEventName);
        Query query = new Query();
        query.addCriteria(where(EntityEventMetaData.CONTEXT_ID_FIELD).is(contextId));
        query.addCriteria(where(EntityEventMetaData.START_TIME_FIELD).is(startTime));
        Update update = new Update();
        update.set(EntityEventMetaData.TRANSMITTED_FIELD, true);
        update.set(EntityEventMetaData.TRANSMISSION_EPOCHTIME_FIELD, transmissionEpochtime);
        mongoTemplate.updateFirst(query, update, collectionName);
    }


    public List<EntityEventMetaData> getEntityEventDataThatWereNotTransmittedOnlyIncludeIdentifyingData(String entityEventName, PageRequest pageRequest) {
        String collectionName = getCollectionName(entityEventName);
        if (mongoDbUtilService.collectionExists(collectionName)) {
            Query query = new Query();
            query.addCriteria(where(EntityEventMetaData.TRANSMITTED_FIELD).is(false));
            query.fields().include(EntityEventMetaData.ENTITY_EVENT_NAME_FIELD);
            query.fields().include(EntityEventMetaData.CONTEXT_ID_FIELD);
            query.fields().include(EntityEventMetaData.START_TIME_FIELD);
            query.fields().include(EntityEventMetaData.END_TIME_FIELD);
            if(pageRequest != null){
                query.with(pageRequest);
            }
            return mongoTemplate.find(query, EntityEventMetaData.class, collectionName);
        }

        return Collections.emptyList();
    }

    public void storeEntityEventData(EntityEventMetaData entityEventMetaData) {
        String entityEventName = entityEventMetaData.getEntityEventName();
        String collectionName = getCollectionName(entityEventName);

        if (!mongoDbUtilService.collectionExists(collectionName)) {
            mongoDbUtilService.createCollection(collectionName);

            // Transmitted + end time
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
                    .on(EntityEventMetaData.TRANSMITTED_FIELD, Sort.Direction.ASC)
                    .on(EntityEventMetaData.END_TIME_FIELD, Sort.Direction.ASC));

            // Context ID + start time
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
                    .on(EntityEventMetaData.CONTEXT_ID_FIELD, Sort.Direction.ASC)
                    .on(EntityEventMetaData.START_TIME_FIELD, Sort.Direction.ASC));
        }

        mongoTemplate.save(entityEventMetaData, collectionName);
    }


    private static String getCollectionName(String entityEventName) {
        return String.format("%s%s", COLLECTION_NAME_PREFIX, entityEventName);
    }
}
