package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.aggr.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.mock;
import static presidio.ade.domain.record.aggregated.AggregatedFeatureType.FEATURE_AGGREGATION;
import static presidio.ade.domain.record.aggregated.AggregatedFeatureType.SCORE_AGGREGATION;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        AggregatedDataStoreConfig.class,
        MongodbTestConfig.class
})
public class AggregatedRecordPaginationServiceTest {
    private static final int AMOUNT_OF_GENERATED_CONTEXT_IDS = 20;
    private static final int AMOUNT_OF_FEATURE_AGGR_PER_CONTEXT_ID = 1;
    private static final int AMOUNT_OF_SCORE_AGGR_PER_CONTEXT_ID = 2;
    private static final int AMOUNT_OF_RECORDS_PER_FEATURE = 2;

    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    private AggrDataToCollectionNameTranslator translator;
    @Autowired
    private MongoTemplate mongoTemplate;

    private Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet;
    private AggregatedRecordPaginationService paginationService;
    private Instant startInstant;
    private Duration strategy;

    @Before
    public void setup() {
        ((AggregatedDataStoreMongoImpl)aggregatedDataStore).setTtlService(mock(TtlService.class));
        aggregatedDataPaginationParamSet = new HashSet<>();
        paginationService = new AggregatedRecordPaginationService(10, aggregatedDataStore);
        startInstant = Instant.EPOCH.plus(Duration.ofDays(1));
        strategy = FixedDurationStrategy.HOURLY.toDuration();
        String featureName = "featureName";
        double featureValue = 5D;

        for (int i = 0; i < AMOUNT_OF_FEATURE_AGGR_PER_CONTEXT_ID; i++) {
            String enumeratedFeatureName = String.format("F_%s%d", featureName, i);
            aggregatedDataPaginationParamSet.add(new AggregatedDataPaginationParam(enumeratedFeatureName, FEATURE_AGGREGATION));

            for (int j = 0; j < AMOUNT_OF_GENERATED_CONTEXT_IDS; j++) {
                Map<String, String> context = new HashMap<>();
                context.put("userId", String.format("Gandalf%d", j));
                List<AdeAggregationRecord> featureAggrRecords = generateFeatureAggrRecords(context, enumeratedFeatureName, featureValue, AMOUNT_OF_RECORDS_PER_FEATURE, startInstant, strategy);
                aggregatedDataStore.store(featureAggrRecords, FEATURE_AGGREGATION);
            }
        }

        for (int i = 0; i < AMOUNT_OF_SCORE_AGGR_PER_CONTEXT_ID; i++) {
            String enumeratedFeatureName = String.format("P_%s%d", featureName, i);
            aggregatedDataPaginationParamSet.add(new AggregatedDataPaginationParam(enumeratedFeatureName, SCORE_AGGREGATION));

            for (int j = 0; j < AMOUNT_OF_GENERATED_CONTEXT_IDS; j++) {
                Map<String, String> context = new HashMap<>();
                context.put("userId", String.format("Gandalf%d", j));
                List<AdeAggregationRecord> scoreAggrRecords = generateScoreAggrRecords(context, enumeratedFeatureName, featureValue, AMOUNT_OF_RECORDS_PER_FEATURE, startInstant, strategy);
                aggregatedDataStore.store(scoreAggrRecords, SCORE_AGGREGATION);
            }
        }
    }

    @After
    public void cleanup() {
        aggregatedDataPaginationParamSet.forEach(aggregatedDataPaginationParam -> {
            String featureName = aggregatedDataPaginationParam.getFeatureName();
            AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
            AggrRecordsMetadata metadata = new AggrRecordsMetadata(featureName, aggregatedFeatureType);
            String collectionName = translator.toCollectionName(metadata);
            mongoTemplate.dropCollection(collectionName);
        });
    }

    private List<AdeAggregationRecord> generateFeatureAggrRecords(Map<String, String> context, String featureName, double featureValue, int amountOfRecords, Instant startInstant, Duration strategy) {
        List<AdeAggregationRecord> records = new LinkedList<>();

        for (int i = 0; i < amountOfRecords; i++) {
            Instant endInstant = startInstant.plus(strategy);
            AdeAggregationRecord record = new ScoredFeatureAggregationRecord(50D, null, startInstant, endInstant, featureName, featureValue, "featureBucketConfName", context, FEATURE_AGGREGATION);
            records.add(record);
            startInstant = endInstant;
        }

        return records;
    }

    private List<AdeAggregationRecord> generateScoreAggrRecords(Map<String, String> context, String featureName, double featureValue, int amountOfRecords, Instant startInstant, Duration strategy) {
        List<AdeAggregationRecord> records = new LinkedList<>();

        for (int i = 0; i < amountOfRecords; i++) {
            Instant endInstant = startInstant.plus(strategy);
            AdeAggregationRecord record = new AdeAggregationRecord(startInstant, endInstant, featureName, featureValue, "featureBucketConfName", context, SCORE_AGGREGATION);
            records.add(record);
            startInstant = endInstant;
        }

        return records;
    }

    @Test
    public void getContextIdToNumOfItemsList() throws Exception {
        TimeRange timeRange = new TimeRange(Instant.EPOCH, Instant.now());
        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = paginationService.getContextIdToNumOfItemsList(aggregatedDataPaginationParamSet, timeRange, null);
        Assert.assertEquals(AMOUNT_OF_GENERATED_CONTEXT_IDS, contextIdToNumOfItemsList.size());
        contextIdToNumOfItemsList.forEach(contextIdToNumOfItems -> Assert.assertEquals(1, contextIdToNumOfItems.getTotalNumOfItems()));
    }

    @Test
    public void getPageIterators() throws Exception {
        for (int i = 0; i < AMOUNT_OF_RECORDS_PER_FEATURE; i++) {
            Instant currentStartTime = startInstant.plus(strategy.multipliedBy(i));
            Instant currentEndTime = currentStartTime.plus(strategy);
            TimeRange timeRange = new TimeRange(currentStartTime, currentEndTime);
            List<PageIterator<AdeAggregationRecord>> pageIterators = paginationService.getPageIterators(aggregatedDataPaginationParamSet, timeRange);
            Assert.assertEquals(2, pageIterators.size());

            pageIterators.forEach(pageIterator -> {
                List<AdeAggregationRecord> records = pageIterator.next();
                Assert.assertEquals(30, records.size());
                boolean hasNext = pageIterator.hasNext();
                Assert.assertFalse(hasNext);
            });
        }
    }
}
