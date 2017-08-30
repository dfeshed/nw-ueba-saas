package presidio.output.domain.repositories;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventMongoRepositoryImpl implements EventRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final List<String> collectionNames = new ArrayList<>(Arrays.asList(
            "output_active_directory_enriched_events","output_authentication_enriched_events","output_file_enriched_events"));

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
        List<EnrichedEvent> enrichedEvents=null;
        for (String collection : collectionNames){
            enrichedEvents = mongoTemplate.find(query, EnrichedEvent.class,collection);
            if (enrichedEvents.size()>0)
                break;
        }
        if (enrichedEvents.size()>0) {
            return enrichedEvents.get(0);
        } else {
            return  null;
        }


    }
}
