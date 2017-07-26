package presidio.ade.domain.store.smart;

import fortscale.domain.SMART.EntityEvent;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

/**
 * Created by efratn on 23/07/2017.
 */
public class SmartDataStoreMongoImpl implements SmartDataStore {

    private final MongoTemplate mongoTemplate;

    public SmartDataStoreMongoImpl(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public List<EntityEvent> readSmarts(TimeRange timeRange, int scoreThreshold) {
            //TODO- ADE team to implement this
        Instant start = timeRange.getStart();
        Instant end = timeRange.getEnd();
        Criteria startDateTimeCriteria = Criteria.where(EntityEvent.ENTITY_EVENT_START_TIME_UNIX_FIELD_NAME).gte(start.getEpochSecond());
        Criteria endDateTimeCriteria = Criteria.where(EntityEvent.ENTITY_EVENT_END_TIME_UNIX_FIELD_NAME).lte(end.getEpochSecond());
        Criteria scoreCriteria = Criteria.where(EntityEvent.ENTITY_EVENT_SCORE_FIELD_NAME).gte(scoreThreshold);
        Query query = new Query(startDateTimeCriteria)
                .addCriteria(endDateTimeCriteria)
                .addCriteria(scoreCriteria);
        String collectionName = "ADE_SMARTS"; //TODO use translator
        //TODO- logging
       return mongoTemplate.find(query, EntityEvent.class, collectionName);
    }

}
