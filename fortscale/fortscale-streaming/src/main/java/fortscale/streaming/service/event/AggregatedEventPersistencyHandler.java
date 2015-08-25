package fortscale.streaming.service.event;


import fortscale.aggregation.feature.event.AggrEvent;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * Created by amira on 23/08/2015.
 */
public class AggregatedEventPersistencyHandler implements EventPersistencyHandler {
    private static final String COLLECTION_NAME_SEPERATOR = "__";

    private MongoTemplate mongoTemplate;
    private String eventTypeFieldName;

    public AggregatedEventPersistencyHandler(MongoTemplate mongoTemplate, String eventTypeFieldName) {
        this.mongoTemplate = mongoTemplate;
        this.eventTypeFieldName = eventTypeFieldName;
    }

    @Override
    public void saveEvent(JSONObject event, String collectionPrefix) {
        String eventTypeValue = (String) event.get(eventTypeFieldName);
        String aggrFeatureName = (String) event.get("aggregated_feature_name");
        String bucketConfName = (String) event.get("bucket_conf_name");
        String collectionName = new StringBuilder(collectionPrefix).append(COLLECTION_NAME_SEPERATOR)
                .append(eventTypeValue).append(COLLECTION_NAME_SEPERATOR)
                .append(aggrFeatureName).toString();


        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_BUCKET_CONF_NAME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_START_TIME_UNIX, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_END_TIME_UNIX, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_CONTEXT, Sort.Direction.DESC));
        }

        mongoTemplate.save(new AggrEvent(event), collectionName);
    }
}
