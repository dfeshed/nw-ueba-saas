package presidio.ade.domain.store.enriched;

import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.scanning.AdeRecordTypeToClass;
import presidio.ade.domain.record.scanning.AdeRecordTypeToClassConfig;
import presidio.ade.domain.store.ContextIdToNumOfEvents;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static presidio.ade.domain.record.enriched.EnrichedDlpFileRecord.NORMALIZED_USERNAME_FIELD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeRecordTypeToClassConfig.class)
public class EnrichedRecordPaginationServiceTest {


    private static final String COLLECTION_NAME_PREFIX = "enriched";
    private EnrichedDataStoreImplMongo enrichedDataStoreImplMongo;

    //list of pair, where pair is of context ids set and pair of amountOfPages, amountOfEvents.
    private List<Pair<Set<String>, Pair<Integer, Integer>>> list;

    @Autowired
    private AdeRecordTypeToClass adeRecordTypeToClass;

    @Before
    /**
     * Create list of test result.
     */
    public void initialize() {
        list = new ArrayList<>();

        Pair<Integer, Integer> numOfPagesToNumOfEvents;
        Pair<Set<String>, Pair<Integer, Integer>> contextIdsPair1, contextIdsPair2, contextIdsPair3;

        Set<String> group1 = new HashSet<>();
        group1.add("a");
        int amountOfPagesINGroup1 = 2;
        int amountOfEventsINGroup1 = 5;
        numOfPagesToNumOfEvents = new Pair<>(amountOfPagesINGroup1, amountOfEventsINGroup1);
        contextIdsPair1 = new Pair<>(group1, numOfPagesToNumOfEvents);

        Set<String> group2 = new HashSet<>();
        group2.add("b");
        group2.add("c");
        int amountOfPagesINGroup2 = 1;
        int amountOfEventsINGroup2 = 3;
        numOfPagesToNumOfEvents = new Pair<>(amountOfPagesINGroup2, amountOfEventsINGroup2);
        contextIdsPair2 = new Pair<>(group2, numOfPagesToNumOfEvents);

        list.add(contextIdsPair1);
        list.add(contextIdsPair2);
    }

    /**
     * Test the enriched record pagination service.
     */
    @Test
    public void test_enriched_record_pagination_service() {
        int pageSize = 3;
        int maxGroupSize = 2;
        Instant now = Instant.now();

        List<ContextIdToNumOfEvents> ContextIdToNumOfEventsList = new ArrayList<>();
        ContextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("a", 5));
        ContextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("b", 2));
        ContextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("c", 1));

        MongoTemplate mongoTemplate = mock(MongoTemplate.class);

        //mock for aggregation
        AggregationResults<ContextIdToNumOfEvents> aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("enriched_dlp_file"), eq(ContextIdToNumOfEvents.class))).thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(ContextIdToNumOfEventsList);

        //mock for index operations
        IndexOperations indexOperations = mock(DefaultIndexOperations.class);
        when(mongoTemplate.indexOps(any(Class.class))).thenReturn(indexOperations);

        //mock for queries
        createQueryForFirstCall(mongoTemplate, now);
        createQueryForSecondCall(mongoTemplate, now);
        createQueryForThirdCall(mongoTemplate, now);

        EnrichedDataToCollectionNameTranslator translator = new EnrichedDataToCollectionNameTranslator();
        enrichedDataStoreImplMongo = new EnrichedDataStoreImplMongo(mongoTemplate, translator, this.adeRecordTypeToClass);

        EnrichedRecordPaginationService paginationService =
                new EnrichedRecordPaginationService(enrichedDataStoreImplMongo, pageSize, maxGroupSize, NORMALIZED_USERNAME_FIELD);

        TimeRange timeRange = new TimeRange(now, now);

        List<PageIterator<EnrichedDlpFileRecord>> pageIterators = paginationService.getPageIterators("dlp_file", timeRange);


        assertTrue(pageIterators.size() == 2);

        Iterator<PageIterator<EnrichedDlpFileRecord>> simpleUserEventPageIterator = pageIterators.iterator();

        //foreach pageIterator get pages.
        // get amount of pages in iterator.
        //foreach page get contextId list and list of events.
        while (simpleUserEventPageIterator.hasNext()) {
            PageIterator<EnrichedDlpFileRecord> pageIterator = simpleUserEventPageIterator.next();
            List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = new ArrayList<>();
            Set<String> contextIdList = new HashSet<>();
            int amountOfPages = 0;

            while (pageIterator.hasNext()) {
                List<EnrichedDlpFileRecord> list = pageIterator.next();
                amountOfPages++;
                for (EnrichedDlpFileRecord enrichedDlpFileRecord : list) {
                    enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
                    String name = enrichedDlpFileRecord.getNormalized_username();
                    contextIdList.add(name);
                }
            }
            assertExpectedResult(contextIdList, enrichedDlpFileRecordList, amountOfPages);
        }
        assertTrue(list.size() == 0);
    }

    /**
     * Assert amount of pages in group, amount of events in group and context ids.     *
     *
     * @param contextIdSet
     * @param simpleUserEventsList
     * @param amountOfPages
     */
    public void assertExpectedResult(Set<String> contextIdSet, List<EnrichedDlpFileRecord> simpleUserEventsList, int amountOfPages) {

        Pair<Set<String>, Pair<Integer, Integer>> itemToRemove = null;
        for (Pair<Set<String>, Pair<Integer, Integer>> pair : list) {
            Set<String> group = pair.getKey();
            if (group.containsAll(contextIdSet)) {
                Pair<Integer, Integer> numOfPagesToNumOfEvents = pair.getValue();
                int amountOfPagesInGroup = numOfPagesToNumOfEvents.getKey();
                int amountOfEventsInGroup = numOfPagesToNumOfEvents.getValue();
                int testEventsNum = simpleUserEventsList.size();
                assertTrue(amountOfPagesInGroup == amountOfPages);
                assertTrue(amountOfEventsInGroup == testEventsNum);
                itemToRemove = pair;
                break;
            }
        }

        //assertFalse if no group was found
        if (itemToRemove != null) {
            list.remove(itemToRemove);
        } else {
            assertFalse(true);
        }
    }

    /**
     * Create appropriate query and result for mongoTemplate.find() method
     *
     * @param mongoTemplate
     * @param now
     */
    private void createQueryForFirstCall(MongoTemplate mongoTemplate, Instant now) {
        Set<String> contextIds = new HashSet<>();
        contextIds.add("a");

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(now).lt(now);
        Criteria contextCriteria = Criteria.where("normalized_username").in(contextIds);
        Query query1 = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(0).limit(3);

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = new ArrayList<>();

        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("a");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
        enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("a");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
        enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("a");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);

        when(mongoTemplate.find(eq(query1), eq(EnrichedDlpFileRecord.class), eq("enriched_dlp_file"))).thenReturn(enrichedDlpFileRecordList);
    }

    /**
     * Create appropriate query and result for mongoTemplate.find() method
     *
     * @param mongoTemplate
     * @param now
     */
    private void createQueryForSecondCall(MongoTemplate mongoTemplate, Instant now) {

        Set<String> contextIds = new HashSet<>();
        contextIds.add("a");

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(now).lt(now);
        Criteria contextCriteria = Criteria.where("normalized_username").in(contextIds);
        Query query2 = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(3).limit(2);

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = new ArrayList<>();
        enrichedDlpFileRecordList = new ArrayList<>();
        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("a");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
        enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("a");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);

        when(mongoTemplate.find(eq(query2), eq(EnrichedDlpFileRecord.class), eq("enriched_dlp_file"))).thenReturn(enrichedDlpFileRecordList);
    }

    /**
     * Create appropriate query and result for mongoTemplate.find() method
     *
     * @param mongoTemplate
     * @param now
     */
    private void createQueryForThirdCall(MongoTemplate mongoTemplate, Instant now) {
        Set<String> contextIds = new HashSet<>();
        contextIds.add("c");
        contextIds.add("b");

        Criteria dateTimeCriteria = Criteria.where(EnrichedRecord.DATE_TIME_FIELD).gte(now).lt(now);
        Criteria contextCriteria = Criteria.where("normalized_username").in(contextIds);
        Query query3 = new Query(dateTimeCriteria).addCriteria(contextCriteria).skip(0).limit(3);

        List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = new ArrayList<>();

        enrichedDlpFileRecordList = new ArrayList<>();
        EnrichedDlpFileRecord enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("c");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
        enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("b");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
        enrichedDlpFileRecord = new EnrichedDlpFileRecord(now);
        enrichedDlpFileRecord.setNormalized_username("b");
        enrichedDlpFileRecordList.add(enrichedDlpFileRecord);

        when(mongoTemplate.find(eq(query3), eq(EnrichedDlpFileRecord.class), eq("enriched_dlp_file"))).thenReturn(enrichedDlpFileRecordList);
    }


}
