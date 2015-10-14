package fortscale.aggregation.feature.event;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation of basic query functionality for aggregated events, based on mongo persistence
 *
 * @author gils
 * Date: 10/09/2015
 */
public class AggregatedEventQueryMongoService implements AggregatedEventQueryService {

    private static final String SCORED_AGGR_EVENT_COLLECTION_PREFIX = "scored___aggr_event__";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<AggrEvent> getAggregatedEventsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime) {
        String collectionName = SCORED_AGGR_EVENT_COLLECTION_PREFIX + featureName;

        if (mongoTemplate.collectionExists(collectionName)) {
            Criteria startTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME_UNIX).gte(TimestampUtils.convertToSeconds(startTime));

            Criteria endTimeCriteria = Criteria.where(AggrEvent.EVENT_FIELD_START_TIME_UNIX).lte(TimestampUtils.convertToSeconds(endTime));

            Criteria contextCriteria = createContextCriteria(contextType, ContextName);

            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria,contextCriteria));

            return mongoTemplate.find(query, AggrEvent.class, collectionName);
        }
        else {
            throw new RuntimeException("Could not fetch aggregated events from collection " + collectionName);
        }
    }

    private Criteria createContextCriteria(String contextType, String contextName) {
        Map<String, String> contextMap = new HashMap<>(1);
        contextMap.put(contextType, contextName);

        return Criteria.where(AggrEvent.EVENT_FIELD_CONTEXT).in(contextMap); // TODO check for multiple context, might not work
    }
}
