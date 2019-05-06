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
import presidio.output.domain.records.events.EnrichedUserEvent;

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
    public List<? extends EnrichedUserEvent> findUserEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int limitEvents) throws Exception {
        Query query = buildQuery(userId, timeRange, features);
        query.limit(limitEvents);

        return mongoTemplate.find(query, EnrichedUserEvent.class, collectionName);
    }

    private Query buildQuery(String userId, TimeRange timeRange, List<Pair<String, Object>> features) {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedUserEvent.USER_ID_FIELD_NAME)
                        .is(userId))
                .addCriteria(Criteria.where(EnrichedUserEvent.EVENT_DATE_FIELD_NAME)
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
        query.with(new Sort(EnrichedUserEvent.EVENT_DATE_FIELD_NAME));
        return query;
    }

    @Override
    public List<? extends EnrichedUserEvent> findUserEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features, int numOfItemsToSkip, int pageSize) {
        Query query = buildQuery(userId, timeRange, features);
        query.skip(numOfItemsToSkip).limit(pageSize);

        return mongoTemplate.find(query, EnrichedUserEvent.class, collectionName);
    }

    @Override
    public long countUserEvents(String collectionName, String userId, TimeRange timeRange, List<Pair<String, Object>> features) {
        Query query = buildQuery(userId, timeRange, features);

        return mongoTemplate.count(query, EnrichedUserEvent.class, collectionName);
    }

    @Override
    public EnrichedUserEvent findLatestEventForUser(String userId, List<String> collectionNamesPrioritized) {
        Query query = new Query()
                .addCriteria(Criteria.where(EnrichedUserEvent.USER_ID_FIELD_NAME).is(userId))
                .limit(1).with(new Sort(Sort.Direction.DESC, EnrichedUserEvent.EVENT_DATE_FIELD_NAME));
        List<EnrichedUserEvent> enrichedUserEvents = null;
        for (String collection : collectionNamesPrioritized) {
            enrichedUserEvents = mongoTemplate.find(query, EnrichedUserEvent.class, collection);
            if (enrichedUserEvents.size() > 0)
                break;
        }
        if (enrichedUserEvents.size() > 0) {
            return enrichedUserEvents.get(0);
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
            return new Query().addCriteria(Criteria.where(EnrichedUserEvent.EVENT_DATE_FIELD_NAME)
                    .lt(endDate));
        } else {
            return new Query().addCriteria(Criteria.where(EnrichedUserEvent.EVENT_DATE_FIELD_NAME)
                    .gte(startDate)
                    .lt(endDate));
        }

    }
}
