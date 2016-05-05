package fortscale.entity.event;


import fortscale.aggregation.util.MongoDbUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class EntityEventMetaDataMongoStore implements EntityEventMetaDataCountReader {
    private static final String COLLECTION_NAME_PREFIX = "entity_event_meta_data_";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Autowired
    private EntityEventConfService entityEventConfService;

    private List<String> allEntityEventMetaDataCollectionNames;


    public void dropAll(){
        for(String collectionName: mongoDbUtilService.getCollections()){
            if(collectionName.startsWith(COLLECTION_NAME_PREFIX)){
                mongoDbUtilService.dropCollection(collectionName);
            }
        }
    }


    public void removeEntityEventMetaData(String entityEventName, String contextId, long startTime) {
        String collectionName = getCollectionName(entityEventName);
        Query query = new Query();
        query.addCriteria(where(EntityEventMetaData.CONTEXT_ID_FIELD).is(contextId));
        query.addCriteria(where(EntityEventMetaData.START_TIME_FIELD).is(startTime));

        mongoTemplate.remove(query, collectionName);
    }


    public List<EntityEventMetaData> getEntityEventMetaData(String entityEventName, PageRequest pageRequest) {
        String collectionName = getCollectionName(entityEventName);
        if (mongoDbUtilService.collectionExists(collectionName)) {
            Query query = new Query();
            if(pageRequest != null){
                query.with(pageRequest);
            }
            return mongoTemplate.find(query, EntityEventMetaData.class, collectionName);
        }

        return Collections.emptyList();
    }

    public void storeEntityEventMetaData(EntityEventMetaData entityEventMetaData) {
        String entityEventName = entityEventMetaData.getEntityEventName();
        String collectionName = getCollectionName(entityEventName);

        if (!mongoDbUtilService.collectionExists(collectionName)) {
            mongoDbUtilService.createCollection(collectionName);

            // end time
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index()
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

    @Override
    public long getTotalNumberOfEntityEventMetaDataEntries() {
        if(allEntityEventMetaDataCollectionNames ==null) {
            allEntityEventMetaDataCollectionNames = new ArrayList<>();
            entityEventConfService.getEntityEventNames().forEach(entityEventName ->
                    allEntityEventMetaDataCollectionNames.add(getCollectionName(entityEventName)));
        }

        long totalNumberOfEvents = 0;

        for(String collectionName: allEntityEventMetaDataCollectionNames) {
            totalNumberOfEvents += mongoTemplate.count(new Query(), collectionName);
        }

        return totalNumberOfEvents;
    }
}
