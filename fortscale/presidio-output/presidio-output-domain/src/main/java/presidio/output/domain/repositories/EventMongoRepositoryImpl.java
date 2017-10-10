package presidio.output.domain.repositories;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventMongoRepositoryImpl implements EventRepository {

    @Value("${output.events.limit: #{100}}")
    private int limitEvents;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    private final List<String> collectionNames = new ArrayList<>(Arrays.asList(
            "output_active_directory_enriched_events", "output_authentication_enriched_events", "output_file_enriched_events"));

    public EventMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveEvents(String collectionName, List<? extends EnrichedEvent> events) throws Exception {
        mongoDbBulkOpUtil.insertUnordered(events, collectionName);
    }

    @Override
    public List<? extends EnrichedEvent> findEvents(String collectionName, String userId, TimeRange timeRange, Map<String, Object> features) throws Exception {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedEvent.USER_ID_FIELD)
                        .is(userId))
                .addCriteria(Criteria.where(EnrichedEvent.START_INSTANT_FIELD)
                        .gte(timeRange.getStart())
                        .lt(timeRange.getEnd()));
        features.forEach((k, v) -> {

            query.addCriteria(new Criteria().orOperator(
                    Criteria.where(k).is(v), // for single object
                    Criteria.where(k).in(v)) // for array
            );

        });
        query.limit(limitEvents);

        return mongoTemplate.find(query, EnrichedEvent.class, collectionName);
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId) {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedEvent.USER_ID_FIELD).is(userId))
                .limit(1);
        List<EnrichedEvent> enrichedEvents = null;
        for (String collection : collectionNames) {
            enrichedEvents = mongoTemplate.find(query, EnrichedEvent.class, collection);
            if (enrichedEvents.size() > 0)
                break;
        }
        if (enrichedEvents.size() > 0) {
            return enrichedEvents.get(0);
        } else {
            return null;
        }
    }
}
