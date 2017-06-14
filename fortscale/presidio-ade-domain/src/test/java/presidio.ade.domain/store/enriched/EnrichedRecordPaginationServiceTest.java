package presidio.ade.domain.store.enriched;

import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import fortscale.utils.pagination.ContextIdToNumOfEvents;
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

    private static final int PAGE_SIZE = 3;
    private static final int MAX_GROUP_SIZE = 2;
    private static final Instant NOW = Instant.now();

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

        //mock of mongoTemplate
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        //mock of aggregation
        createMockForAggregation(mongoTemplate);
        //mock of index operations
        createMockForIndexOperation(mongoTemplate);
        //mock of queries
        createQueryForFirstCall(mongoTemplate, NOW);
        createQueryForSecondCall(mongoTemplate, NOW);
        createQueryForThirdCall(mongoTemplate, NOW);

        //create store
        EnrichedDataToCollectionNameTranslator translator = new EnrichedDataToCollectionNameTranslator();
        enrichedDataStoreImplMongo = new EnrichedDataStoreImplMongo(mongoTemplate, translator, this.adeRecordTypeToClass);

        //create pagination service
        EnrichedRecordPaginationService paginationService =
                new EnrichedRecordPaginationService(enrichedDataStoreImplMongo, PAGE_SIZE, MAX_GROUP_SIZE, NORMALIZED_USERNAME_FIELD);

        TimeRange timeRange = new TimeRange(NOW, NOW);

        List<PageIterator<EnrichedDlpFileRecord>> pageIterators = paginationService.getPageIterators("dlp_file", timeRange);

        //assert number of iterators
        assertTrue(pageIterators.size() == 2);

        //Go over page iterators and check the results.
        checkPageIterators(pageIterators);
    }

    /**
     * Get pages of pageIterator, get amount of pages in iterator.
     * Get context ids and events from each page.
     * Call assertExpectedResult method in order to assert expected results.
     *
     * @param pageIterators list of PageIterators
     */
    private void checkPageIterators(List<PageIterator<EnrichedDlpFileRecord>> pageIterators) {
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
        //assert that the list is empty
        //if the list is not empty, it means that not all pages was created.
        assertTrue(list.size() == 0);
    }

    /**
     * Assert amount of pages in group, amount of events in group and context ids.     *
     *
     * @param contextIdSet
     * @param simpleUserEventsList
     * @param amountOfPages
     */
    private void assertExpectedResult(Set<String> contextIdSet, List<EnrichedDlpFileRecord> simpleUserEventsList, int amountOfPages) {

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

    private void createMockForIndexOperation(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mock(DefaultIndexOperations.class);
        when(mongoTemplate.indexOps(any(Class.class))).thenReturn(indexOperations);
    }

    private void createMockForAggregation(MongoTemplate mongoTemplate) {

        List<ContextIdToNumOfEvents> contextIdToNumOfEventsList = new ArrayList<>();
        contextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("a", 5));
        contextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("b", 2));
        contextIdToNumOfEventsList.add(new ContextIdToNumOfEvents("c", 1));

        AggregationResults<ContextIdToNumOfEvents> aggregationResults = mock(AggregationResults.class);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("enriched_dlp_file"), eq(ContextIdToNumOfEvents.class))).thenReturn(aggregationResults);
        when(aggregationResults.getMappedResults()).thenReturn(contextIdToNumOfEventsList);
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
