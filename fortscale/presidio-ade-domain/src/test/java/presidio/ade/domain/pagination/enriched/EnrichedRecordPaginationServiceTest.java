package presidio.ade.domain.pagination.enriched;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.pagination.enriched.groups.EnrichedRecordPaginationServiceGroup;
import presidio.ade.domain.pagination.enriched.mocks.GenerateMocks;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolverConfig;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;

import java.time.Instant;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AdeEventTypeToAdeEnrichedRecordClassResolverConfig.class, MongoDbBulkOpUtilConfig.class, MongodbTestConfig.class})
public class EnrichedRecordPaginationServiceTest {

    private static final int PAGE_SIZE = 3;
    private static final int MAX_GROUP_SIZE = 2;
    private static final Instant NOW = Instant.now();
    private EnrichedDataStoreImplMongo enrichedDataStoreImplMongo;
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;

    @Before
    public void initialize() {
        //mock of mongoTemplate
        mongoTemplate = mock(MongoTemplate.class);
    }

    /**
     * input:
     * a - 5 events
     * b - 2 events
     * c - 1 events
     * result:
     * {a} -> 2 pages
     * {b, c} -> 1 page
     */
    @Test
    public void test_1() {

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = new ArrayList<>();
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("a", 5));
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("b", 2));
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("c", 1));

        // result for first query
        ArrayList<String> enrichedDlpFileRecordsQuery1 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery1.add("a");
        enrichedDlpFileRecordsQuery1.add("a");
        enrichedDlpFileRecordsQuery1.add("a");

        Set<String> contextIdsQuery1 = new HashSet<>();
        contextIdsQuery1.add("a");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery1, contextIdsQuery1, 0, PAGE_SIZE, NOW);

        // result for second query
        ArrayList<String> enrichedDlpFileRecordsQuery2 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery2.add("a");
        enrichedDlpFileRecordsQuery2.add("a");
        Set<String> contextIdsQuery2 = new HashSet<>();
        contextIdsQuery2.add("a");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery2, contextIdsQuery2, 3, PAGE_SIZE, NOW);

        // result for third query
        ArrayList<String> enrichedDlpFileRecordsQuery3 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery3.add("c");
        enrichedDlpFileRecordsQuery3.add("b");
        enrichedDlpFileRecordsQuery3.add("b");
        Set<String> contextIdsQuery3 = new HashSet<>();
        contextIdsQuery3.add("c");
        contextIdsQuery3.add("b");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery3, contextIdsQuery3, 0, PAGE_SIZE, NOW);

        List<EnrichedRecordPaginationServiceGroup> results = generateResultTest1();

        CreatePageIterators(mongoTemplate, contextIdToNumOfItemsList, results);
    }

    /**
     * input:
     * a - 5 events
     * b - 2 events
     * c - 2 events
     * result:
     * {a} -> 2 pages
     * {b} -> 1 page
     * {c} -> 1 page
     */
    @Test
    public void test_2() {

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = new ArrayList<>();
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("a", 5));
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("b", 2));
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("c", 2));

        // result for first query
        ArrayList<String> enrichedDlpFileRecordsQuery1 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery1.add("a");
        enrichedDlpFileRecordsQuery1.add("a");
        enrichedDlpFileRecordsQuery1.add("a");

        Set<String> contextIdsQuery1 = new HashSet<>();
        contextIdsQuery1.add("a");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery1, contextIdsQuery1, 0, PAGE_SIZE, NOW);

        // result for second query
        ArrayList<String> enrichedDlpFileRecordsQuery2 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery2.add("a");
        enrichedDlpFileRecordsQuery2.add("a");
        Set<String> contextIdsQuery2 = new HashSet<>();
        contextIdsQuery2.add("a");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery2, contextIdsQuery2, 3, PAGE_SIZE, NOW);

        // result for third query
        ArrayList<String> enrichedDlpFileRecordsQuery3 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery3.add("b");
        enrichedDlpFileRecordsQuery3.add("b");
        Set<String> contextIdsQuery3 = new HashSet<>();
        contextIdsQuery3.add("b");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery3, contextIdsQuery3, 0, PAGE_SIZE, NOW);

        // result for fourth query
        ArrayList<String> enrichedDlpFileRecordsQuery4 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery4.add("c");
        enrichedDlpFileRecordsQuery4.add("c");
        Set<String> contextIdsQuery4 = new HashSet<>();
        contextIdsQuery4.add("c");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery4, contextIdsQuery4, 0, PAGE_SIZE, NOW);

        List<EnrichedRecordPaginationServiceGroup> results = generateResultTest2();

        CreatePageIterators(mongoTemplate, contextIdToNumOfItemsList, results);
    }

    /**
     * input:
     * a - 1 events
     * result:
     * {a} -> 1 pages
     */
    @Test
    public void test_3() {

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = new ArrayList<>();
        contextIdToNumOfItemsList.add(new ContextIdToNumOfItems("a", 1));

        // result for first query
        ArrayList<String> enrichedDlpFileRecordsQuery1 = new ArrayList<String>();
        enrichedDlpFileRecordsQuery1.add("a");
        Set<String> contextIdsQuery1 = new HashSet<>();
        contextIdsQuery1.add("a");
        GenerateMocks.createMockForQuery(mongoTemplate, enrichedDlpFileRecordsQuery1, contextIdsQuery1, 0, PAGE_SIZE, NOW);

        List<EnrichedRecordPaginationServiceGroup> results = generateResultTest3();

        CreatePageIterators(mongoTemplate, contextIdToNumOfItemsList, results);
    }

    /**
     * input: {}
     * result: {}
     */
    @Test
    public void test_4() {

        List<EnrichedRecordPaginationServiceGroup> results = generateResultTest4();

        CreatePageIterators(mongoTemplate, new ArrayList<>(), results);
    }

    /**
     * Create list of test result.
     * {a} -> 2 pages, 5 events
     * {b, c} -> 1 page, 3 events
     */
    public List<EnrichedRecordPaginationServiceGroup> generateResultTest1() {
        List<EnrichedRecordPaginationServiceGroup> groups = new ArrayList<>();

        Set<String> group1 = new HashSet<>();
        group1.add("a");
        groups.add(new EnrichedRecordPaginationServiceGroup(5, 2, group1));

        Set<String> group2 = new HashSet<>();
        group2.add("b");
        group2.add("c");
        groups.add(new EnrichedRecordPaginationServiceGroup(3, 1, group2));

        return groups;
    }

    /**
     * Create list of test result.
     * {a} -> 2 pages, 5 events
     * {b} -> 1 page, 2 events
     * {c} -> 1 page, 2 events
     */
    public List<EnrichedRecordPaginationServiceGroup> generateResultTest2() {
        List<EnrichedRecordPaginationServiceGroup> groups = new ArrayList<>();

        Set<String> group1 = new HashSet<>();
        group1.add("a");
        groups.add(new EnrichedRecordPaginationServiceGroup(5, 2, group1));


        Set<String> group2 = new HashSet<>();
        group2.add("b");
        groups.add(new EnrichedRecordPaginationServiceGroup(2, 1, group2));


        Set<String> group3 = new HashSet<>();
        group3.add("c");
        groups.add(new EnrichedRecordPaginationServiceGroup(2, 1, group3));

        return groups;
    }

    /**
     * Create list of test result.
     * {a} -> 1 pages, 1 events
     */
    public List<EnrichedRecordPaginationServiceGroup> generateResultTest3() {
        List<EnrichedRecordPaginationServiceGroup> groups = new ArrayList<>();

        Set<String> group1 = new HashSet<>();
        group1.add("a");
        groups.add(new EnrichedRecordPaginationServiceGroup(1, 1, group1));

        return groups;
    }

    /**
     * Create list of test result.
     * result: {} -> 0 pages, 0 events
     */
    public List<EnrichedRecordPaginationServiceGroup> generateResultTest4() {
        return new ArrayList<>();
    }

    /**
     * Test the enriched record pagination service.
     */
    public void CreatePageIterators(MongoTemplate mongoTemplate, List<ContextIdToNumOfItems> contextIdToNumOfItemsList, List<EnrichedRecordPaginationServiceGroup> results) {

        //mock of aggregation
        GenerateMocks.createMockForAggregation(mongoTemplate, contextIdToNumOfItemsList);
        //mock of index operations
        GenerateMocks.createMockForIndexOperation(mongoTemplate);

        //create store
        EnrichedDataAdeToCollectionNameTranslator translator = new EnrichedDataAdeToCollectionNameTranslator();
        enrichedDataStoreImplMongo = new EnrichedDataStoreImplMongo(mongoTemplate, translator, this.adeEventTypeToAdeEnrichedRecordClassResolver, mongoDbBulkOpUtil, 1);

        //create pagination service
        EnrichedRecordPaginationService paginationService =
                new EnrichedRecordPaginationService(enrichedDataStoreImplMongo, PAGE_SIZE, MAX_GROUP_SIZE, EnrichedDlpFileRecord.USER_ID_FIELD);

        TimeRange timeRange = new TimeRange(NOW, NOW);

        List<PageIterator<EnrichedDlpFileRecord>> pageIterators = paginationService.getPageIterators("dlpfile", timeRange);

        //assert number of iterators
        assertTrue(pageIterators.size() == results.size());

        //Go over page iterators and check the results.
        checkPageIterators(pageIterators, results);
    }

    /**
     * Get pages of pageIterator, get amount of pages in iterator.
     * Get context ids and events from each page.
     * Call assertExpectedResult method in order to assert expected results.
     *
     * @param pageIterators list of PageIterators
     */
    private void checkPageIterators(List<PageIterator<EnrichedDlpFileRecord>> pageIterators, List<EnrichedRecordPaginationServiceGroup> results) {
        Iterator<PageIterator<EnrichedDlpFileRecord>> simpleUserEventPageIterator = pageIterators.iterator();

        //foreach pageIterator get pages.
        // get amount of pages in iterator.
        //foreach page get contextId list and list of events.
        for (PageIterator<EnrichedDlpFileRecord> pageIterator : pageIterators) {
            List<EnrichedDlpFileRecord> enrichedDlpFileRecordList = new ArrayList<>();
            Set<String> contextIdList = new HashSet<>();
            int amountOfPages = 0;

            while (pageIterator.hasNext()) {
                List<EnrichedDlpFileRecord> list = pageIterator.next();
                amountOfPages++;
                for (EnrichedDlpFileRecord enrichedDlpFileRecord : list) {
                    enrichedDlpFileRecordList.add(enrichedDlpFileRecord);
                    String name = enrichedDlpFileRecord.getUserId();
                    contextIdList.add(name);
                }
            }
            assertExpectedResult(contextIdList, enrichedDlpFileRecordList, amountOfPages, results);
        }
        //assert that the list is empty
        //if the list is not empty, it means that not all pages was created.
        assertTrue(results.size() == 0);
    }

    /**
     * Assert amount of pages in group, amount of events in group and context ids.     *
     *
     * @param contextIdSet
     * @param enrichedDlpFileRecords
     * @param amountOfPages
     */
    private void assertExpectedResult(Set<String> contextIdSet, List<EnrichedDlpFileRecord> enrichedDlpFileRecords, int amountOfPages, List<EnrichedRecordPaginationServiceGroup> results) {

        EnrichedRecordPaginationServiceGroup itemToRemove = null;
        for (EnrichedRecordPaginationServiceGroup group : results) {
            if (group.getContextIds().containsAll(contextIdSet)) {
                int testEventsNum = enrichedDlpFileRecords.size();
                assertTrue(group.getNumOfPages() == amountOfPages);
                assertTrue(group.getNumOfEvents() == testEventsNum);
                itemToRemove = group;
                break;
            }
        }

        //assertFalse if no group was found
        if (itemToRemove != null) {
            results.remove(itemToRemove);
        } else {
            assertFalse(true);
        }
    }

}
