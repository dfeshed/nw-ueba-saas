package presidio.ade.domain.store.enriched.mocks;

import fortscale.utils.pagination.ContextIdToNumOfEvents;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.h2.expression.Function.NOW;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Generate aggregation mock, query mock and index Operation mock for test
 */
public class GenerateMocks {
    /**
     * Create ndex Operation mock
     *
     * @param mongoTemplate
     */
    public static void createMockForIndexOperation(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mock(DefaultIndexOperations.class);
        when(mongoTemplate.indexOps(any(Class.class))).thenReturn(indexOperations);
    }

    /**
     * Create aggregation mock
     *
     * @param mongoTemplate
     * @param contextIdToNumOfEventsList list of contextIdToNumOfEvents
     */
    public static void createMockForAggregation(MongoTemplate mongoTemplate, List<ContextIdToNumOfEvents> contextIdToNumOfEventsList) {
        AggregationResults<ContextIdToNumOfEvents> aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("enriched_dlpfile"), eq(ContextIdToNumOfEvents.class))).thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(contextIdToNumOfEventsList);
    }

    /**
     * Create query mock
     *
     * @param mongoTemplate
     */
    public static void createMockForQuery(MongoTemplate mongoTemplate, List<String> enrichedDlpFileRecords, Set<String> contextIdsResult, int skip, int limit, Instant now) {

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(now).lt(now);
        Criteria contextCriteria = Criteria.where("normalized_username").in(contextIdsResult);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(skip).limit(limit);

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordsResult = new ArrayList<>();
        for (String item : enrichedDlpFileRecords) {
            EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
            enrichedDlpFileRecord.setNormalized_username(item);
            enrichedDlpFileRecordsResult.add(enrichedDlpFileRecord);
        }

        when(mongoTemplate.find(eq(query), eq(EnrichedDlpFileRecord.class), eq("enriched_dlpfile"))).thenReturn(enrichedDlpFileRecordsResult);
    }

}
