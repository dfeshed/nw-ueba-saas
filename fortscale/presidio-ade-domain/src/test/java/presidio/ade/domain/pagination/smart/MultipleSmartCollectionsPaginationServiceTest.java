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
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartDataReaderConfig;
import presidio.ade.domain.store.smart.SmartDataToCollectionNameTranslator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author Maria Dorohin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongodbTestConfig.class, SmartDataReaderConfig.class})
public class MultipleSmartCollectionsPaginationServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SmartDataReader smartDataReader;

    private Set<String> configurationNames;
    private MultipleSmartCollectionsPaginationService paginationService;

    @Before
    public void setup() {
        configurationNames = new HashSet<>(Arrays.asList("smartRecordConf1", "smartRecordConf2"));
        paginationService = new MultipleSmartCollectionsPaginationService(configurationNames, smartDataReader, 3, 100);
    }

    @Test
    public void getPageIteratorWhenThereAreSmartCollections() {
        List<Pair<String, Double>> contextIdAndScorePairs = new ArrayList<>();
        contextIdAndScorePairs.add(new Pair<>("userId#testUser1", 90.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser2", 50.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser3", 70.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser4", 30.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser5", 55.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser6", 65.0));
        contextIdAndScorePairs.add(new Pair<>("userId#testUser7", 45.0));

        Instant start = Instant.EPOCH.plus(Duration.ofDays(1));
        Instant end = start.plus(Duration.ofHours(1));
        TimeRange timeRange = new TimeRange(start, end);
        List<SmartRecord> smartRecords = new ArrayList<>();

        for (Pair<String, Double> contextIdAndScorePair : contextIdAndScorePairs) {
            SmartRecord smartRecord = new SmartRecord(
                    timeRange, contextIdAndScorePair.getKey(), "featureName", FixedDurationStrategy.HOURLY,
                    0.5, contextIdAndScorePair.getValue(), Collections.emptyList(), Collections.emptyList());
            smartRecords.add(smartRecord);
        }

        for (String configurationName : configurationNames) {
            mongoTemplate.insert(smartRecords, SmartDataToCollectionNameTranslator.SMART_COLLECTION_PREFIX + configurationName);
        }

        mongoTemplate.insert(smartRecords, "notASmartCollection");
        PageIterator<SmartRecord> pageIterator = paginationService.getPageIterator(new TimeRange(Instant.EPOCH, Instant.now()), 40);
        int numOfPages = 0;

        while (pageIterator.hasNext()) {
            List<SmartRecord> nextPage = pageIterator.next();
            numOfPages++;
            Assert.assertEquals(3, nextPage.size());
            nextPage.forEach(record -> Assert.assertTrue(record.getScore() >= 40));
        }

        // 2 smart collections, 2 pages each
        Assert.assertEquals(4, numOfPages);
    }

    @Test
    public void getPageIteratorWhenThereAreNoSmartCollections() {
        mongoTemplate.getCollectionNames().forEach(collectionName -> mongoTemplate.dropCollection(collectionName));
        TimeRange timeRange = new TimeRange(Instant.EPOCH, Instant.now());
        PageIterator<SmartRecord> pageIterator = paginationService.getPageIterator(timeRange, 0);
        Assert.assertFalse(pageIterator.hasNext());
    }
}
