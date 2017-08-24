package presidio.ade.domain.pagination.smart;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataToCollectionNameTranslator;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


/**
 * Created by maria_dorohin on 8/22/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        SmartDataReaderConfig.class,
        MongodbTestConfig.class
})
public class ScoreThresholdSmartPaginationServiceTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SmartDataReader smartDataReader;

    private ScoreThresholdSmartPaginationService paginationService;

    public void setup() {
        paginationService = new ScoreThresholdSmartPaginationService(smartDataReader, 3);

        List<SmartRecord> smartRecords = new ArrayList<>();

        TimeRange timeRange = new TimeRange(Instant.EPOCH.plus(Duration.ofDays(1)), Instant.now().minus(Duration.ofDays(1)));
        List<Pair<String, Double>> usersToScoreList = new ArrayList<>();
        usersToScoreList.add(new Pair<>("userTest1", 90.0));
        usersToScoreList.add(new Pair<>("userTest2", 50.0));
        usersToScoreList.add(new Pair<>("userTest3", 70.0));
        usersToScoreList.add(new Pair<>("userTest4", 30.0));
        usersToScoreList.add(new Pair<>("userTest5", 55.0));
        usersToScoreList.add(new Pair<>("userTest6", 65.0));
        usersToScoreList.add(new Pair<>("userTest7", 45.0));
        usersToScoreList.add(new Pair<>("userTest7", 85.0));


        for (Pair<String, Double> usersToScore : usersToScoreList) {
            SmartRecord smartRecord = new SmartRecord(timeRange, usersToScore.getKey(), "featureName", FixedDurationStrategy.HOURLY,
                    90, usersToScore.getValue(), Collections.emptyList(), Collections.emptyList());
            smartRecords.add(smartRecord);
        }

        Set<String> collections = new HashSet<>();
        collections.add("CollectionName1");
        collections.add("CollectionName2");
        for (String collectionName : collections) {
            mongoTemplate.insert(smartRecords, SmartDataToCollectionNameTranslator.SMART_COLLECTION_PREFIX + collectionName);
        }

        mongoTemplate.insert(smartRecords, "NotSmartCollectionName");
    }

    /**
     * Creates PageIterator, which contains 3 SmartRecordPageIterator.
     */
    @Test
    public void getPageIterator() {
        setup();
        TimeRange timeRange = new TimeRange(Instant.EPOCH, Instant.now());

        PageIterator<SmartRecord> pageIterator = paginationService.getPageIterator(timeRange, 40);
        while (pageIterator.hasNext()) {
            List<SmartRecord> smartRecords = pageIterator.next();
            Assert.assertTrue("page must not be empty", smartRecords.size() > 0);
            smartRecords.forEach(record -> {
                Assert.assertTrue("score must be greater than 40", record.getScore() > 40);
            });
        }
    }


    /**
     * Test ScoreThresholdSmartPaginationService, where no smart collection exist
     */
    @Test
    public void getPageIteratorWithoutSmartCollections() {
        TimeRange timeRange = new TimeRange(Instant.EPOCH, Instant.now());
        paginationService = new ScoreThresholdSmartPaginationService(smartDataReader, 3);
        PageIterator<SmartRecord> pageIterator = paginationService.getPageIterator(timeRange, 40);
        Assert.assertFalse(pageIterator.hasNext());
    }
}
