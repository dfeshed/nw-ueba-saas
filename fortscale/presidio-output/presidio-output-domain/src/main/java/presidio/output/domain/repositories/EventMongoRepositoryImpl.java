package presidio.output.domain.repositories;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import presidio.output.domain.records.events.EnrichedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
public class EventMongoRepositoryImpl implements EventRepository {

    private static final Logger logger = Logger.getLogger(EventMongoRepositoryImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public EventMongoRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveEvents(String collectionName, List<? extends EnrichedEvent> events) throws Exception {
        mongoDbBulkOpUtil.insertUnordered(events, collectionName);
    }

    @Override
    public List<? extends EnrichedEvent> findEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int limitEvents) throws Exception {
        Query query = buildQuery(userId, timeRange, features);
        query.limit(limitEvents);

        return mongoTemplate.find(query, EnrichedEvent.class, collectionName);
    }

    private Query buildQuery(String userId, TimeRange timeRange, List<Pair<String, Object>> features) {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedEvent.USER_ID_FIELD)
                        .is(userId))
                .addCriteria(Criteria.where(EnrichedEvent.START_INSTANT_FIELD)
                        .gte(timeRange.getStart())
                        .lt(timeRange.getEnd()));

        List<Criteria> criterias = new ArrayList<>();
        features.forEach((feature) -> {
            criterias.add(new Criteria().orOperator(
                    Criteria.where(feature.getFirst()).is(feature.getSecond()), // for single object
                    Criteria.where(feature.getFirst()).in(feature.getSecond())) // for array
            );

        });
        if (CollectionUtils.isNotEmpty(criterias)) {
            query.addCriteria(new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()])));
        }
        query.with(new Sort(EnrichedEvent.START_INSTANT_FIELD));
        return query;
    }

    @Override
    public List<? extends EnrichedEvent> findEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize) {
        Query query = buildQuery(userId, timeRange, features);
        query.skip(numOfItemsToSkip).limit(pageSize);

        return mongoTemplate.find(query, EnrichedEvent.class, collectionName);
    }

    @Override
    public EnrichedEvent findLatestEventForUser(String userId, List<String> collectionNames) {
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

    @Override
    public void remove(String collectionName, Instant startDate, Instant endDate) {
        logger.debug("Deleting events of collection {}, from {} until date {}", collectionName, startDate, endDate);
        Query query = createDateRangeQuery(startDate, endDate);
        mongoTemplate.remove(query, collectionName);
    }


    private Query createDateRangeQuery(Instant startDate, Instant endDate) {
        if (startDate.equals(Instant.EPOCH)) {
            return new Query().addCriteria(Criteria.where(EnrichedEvent.START_INSTANT_FIELD)
                    .lt(endDate));
        } else {
            return new Query().addCriteria(Criteria.where(EnrichedEvent.START_INSTANT_FIELD)
                    .gte(startDate)
                    .lt(endDate));
        }

    }
}
