package presidio.ade.domain.pagination.enriched.mocks;

import fortscale.utils.pagination.ContextIdToNumOfItems;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        when(mongoTemplate.indexOps(eq("enriched_dlpfile"))).thenReturn(indexOperations);
    }

    /**
     * Create aggregation mock
     *
     * @param mongoTemplate
     * @param contextIdToNumOfItemsList list of ContextIdToNumOfItems
     */
    public static void createMockForAggregation(MongoTemplate mongoTemplate, List<ContextIdToNumOfItems> contextIdToNumOfItemsList) {
        AggregationResults<ContextIdToNumOfItems> aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("enriched_dlpfile"), eq(ContextIdToNumOfItems.class))).thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(contextIdToNumOfItemsList);
    }

    /**
     * Create query mock
     *
     * @param mongoTemplate
     */
    public static void createMockForQuery(MongoTemplate mongoTemplate, List<String> enrichedDlpFileRecords, Set<String> contextIdsResult, int skip, int limit, Instant now) {

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.START_INSTANT_FIELD).gte(now).lt(now);
        Criteria contextCriteria = Criteria.where("userId").in(contextIdsResult);
        Query query = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(skip).limit(limit);

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordsResult = new ArrayList<>();
        for (String item : enrichedDlpFileRecords) {
            EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
            enrichedDlpFileRecord.setUserId(item);
            enrichedDlpFileRecordsResult.add(enrichedDlpFileRecord);
        }

        when(mongoTemplate.find(eq(query), eq(EnrichedDlpFileRecord.class), eq("enriched_dlpfile"))).thenReturn(enrichedDlpFileRecordsResult);
    }

}
