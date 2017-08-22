package presidio.output.domain.repositories;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventMongoRepositoryImpl implements EventRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public EventMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveEvents(String collectionName, List<? extends EnrichedEvent> events) throws Exception{
        mongoTemplate.insert(events, collectionName);
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId) {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedEvent.USER_ID_FIELD).is(userId))
                .limit(1);

        List<EnrichedEvent> enrichedEvents = mongoTemplate.find(query, EnrichedEvent.class);
        return enrichedEvents.get(0);
    }
}
