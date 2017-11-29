package presidio.ade.domain.pagination.enriched;

import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtilConfig;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.pagination.enriched.groups.EnrichedRecordPaginationServiceGroup;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolver;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeEnrichedRecordClassResolverConfig;
import presidio.ade.domain.record.enriched.dlpfile.EnrichedDlpFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mariad on 6/15/2017.
 */
@ContextConfiguration(classes = {MongodbTestConfig.class, AdeEventTypeToAdeEnrichedRecordClassResolverConfig.class, MongoDbBulkOpUtilConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Category(ModuleTestCategory.class)
public class FakeMongoEnrichedRecordPaginationServiceTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private AdeEventTypeToAdeEnrichedRecordClassResolver adeEventTypeToAdeEnrichedRecordClassResolver;
    @Autowired
    private MongoDbBulkOpUtil mongoDbBulkOpUtil;


    private static final int PAGE_SIZE = 4;
    private static final int MAX_GROUP_SIZE = 2;
    private static final Instant EVENT_DATE = Instant.ofEpochSecond(1451606400);
    private static final Instant START = Instant.ofEpochSecond(1388534400);
    private static final Instant END = Instant.ofEpochSecond(1483228800);
    private EnrichedDataStoreImplMongo enrichedDataStoreImplMongo;

    /**
     * Test enriched pagination service by using fake mongo
     */
    @Test
    public void test() {
        mongoTemplate.createCollection("enriched_dlpfile");
        mongoTemplate.getCollectionNames();
        EnrichedDlpFileRecord e = new EnrichedDlpFileRecord(EVENT_DATE);
        e.setUserId("a");
        EnrichedDlpFileRecord e1 = new EnrichedDlpFileRecord(EVENT_DATE);
        e1.setUserId("b");

        List<EnrichedDlpFileRecord> records = new ArrayList<>();
        records.add(e);
        records.add(e1);
        records.add(e1);

        mongoTemplate.insert(records, "enriched_dlpfile");

        //create store
        EnrichedDataAdeToCollectionNameTranslator translator = new EnrichedDataAdeToCollectionNameTranslator();
        enrichedDataStoreImplMongo = new EnrichedDataStoreImplMongo(mongoTemplate, translator, this.adeEventTypeToAdeEnrichedRecordClassResolver, mongoDbBulkOpUtil, 1);

        //create pagination service
        EnrichedRecordPaginationService paginationService =
                new EnrichedRecordPaginationService(enrichedDataStoreImplMongo, PAGE_SIZE, MAX_GROUP_SIZE, EnrichedDlpFileRecord.USER_ID_FIELD);

        TimeRange timeRange = new TimeRange(START, END);

        List<PageIterator<EnrichedDlpFileRecord>> pageIterators = paginationService.getPageIterators("dlpfile", timeRange);

        //Go over page iterators and check the results.
        checkPageIterators(pageIterators);
    }


    private void checkPageIterators(List<PageIterator<EnrichedDlpFileRecord>> pageIterators) {

        List<EnrichedRecordPaginationServiceGroup> results = generateResultTest2();

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
        assertTrue(results.size() == 0);
    }


    /**
     * Create list of test result.
     * {a,b} -> 1 pages, 3 events
     */
    public List<EnrichedRecordPaginationServiceGroup> generateResultTest2() {
        List<EnrichedRecordPaginationServiceGroup> groups = new ArrayList<>();

        Set<String> group = new HashSet<>();
        group.add("a");
        group.add("b");
        group.add("b");
        groups.add(new EnrichedRecordPaginationServiceGroup(3, 1, group));

        return groups;
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
