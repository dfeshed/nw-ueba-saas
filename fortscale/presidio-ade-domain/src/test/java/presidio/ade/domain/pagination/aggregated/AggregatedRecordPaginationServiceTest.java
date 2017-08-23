package presidio.ade.domain.pagination.aggregated;

import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
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
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.domain.store.aggr.AggregatedDataStoreConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static presidio.ade.domain.record.aggregated.AggregatedFeatureType.FEATURE_AGGREGATION;
import static presidio.ade.domain.record.aggregated.AggregatedFeatureType.SCORE_AGGREGATION;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        AggregatedDataStoreConfig.class,
        MongodbTestConfig.class
})
public class AggregatedRecordPaginationServiceTest {
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    private AggrDataToCollectionNameTranslator translator;
    @Autowired
    private MongoTemplate mongoTemplate;

    private HashSet<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet;
    private AggregatedRecordPaginationService paginationService;
    private final int AMOUNT_OF_GENERATED_CONTEXTS = 15;
    private final int AMOUNT_OF_SCORE_AGGR_PER_CONTEXT = 2;
    private final int AMOUNT_OF_RECORDS_PER_FEATURE = 2;
    private final int AMOUNT_OF_FEATURE_AGGR_PER_CONTEXT = 1;
    private Instant startInstant;
    private Instant endInstant;

    @Before
    public void setup()
    {
        paginationService = new AggregatedRecordPaginationService(10,aggregatedDataStore);
        this.startInstant = Instant.EPOCH.plus(Duration.ofDays(1));
        Instant startInstant = this.startInstant;
        this.endInstant = startInstant.plus(Duration.ofHours(1));
        Instant endInstant = this.endInstant;
        String featureName = "featureName";

        aggregatedDataPaginationParamSet = new HashSet<>();
        for(int i = 0; i< AMOUNT_OF_GENERATED_CONTEXTS; i++)
        {
            Map<String,String> context= new HashMap<>();
            context.put("userId",String.format("Gandalf%d",i));
            double featureValue = 5D;

            for (int featureNameCnt=0; featureNameCnt<AMOUNT_OF_SCORE_AGGR_PER_CONTEXT; featureNameCnt++)
            {
                String enumeratedFeatureName = String.format("P_%s%d", featureName, featureNameCnt);

                List<AdeAggregationRecord> scoreAggrRecords = generateUserScoreAggrRecord(context, enumeratedFeatureName, featureValue, AMOUNT_OF_RECORDS_PER_FEATURE, startInstant, endInstant);
                addRecordsToPaginationParamsSet(scoreAggrRecords);
                aggregatedDataStore.store(scoreAggrRecords,SCORE_AGGREGATION);
            }
            for (int featureNameCnt = 0; featureNameCnt<AMOUNT_OF_FEATURE_AGGR_PER_CONTEXT; featureNameCnt++)
            {
                String enumeratedFeatureName = String.format("F_%s%d", featureName, featureNameCnt);
                List<AdeAggregationRecord> featureAggrRecord = generateUserFeatureAggrRecord(context, enumeratedFeatureName, featureValue, AMOUNT_OF_RECORDS_PER_FEATURE, startInstant, endInstant);
                addRecordsToPaginationParamsSet(featureAggrRecord);
                aggregatedDataStore.store(featureAggrRecord,FEATURE_AGGREGATION);
            }
        }
    }

    @After
    public void cleanup()
    {
        aggregatedDataPaginationParamSet.forEach( aggregatedDataPaginationParam -> {
            String featureName = aggregatedDataPaginationParam.getFeatureName();
            AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
            AggrRecordsMetadata metadata = new AggrRecordsMetadata(featureName,aggregatedFeatureType);
            String collectionName = translator.toCollectionName(metadata);
            mongoTemplate.dropCollection(collectionName);
        });
    }

    private void addRecordsToPaginationParamsSet(List<AdeAggregationRecord> records) {
        records.forEach(record -> {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(record.getFeatureName(),record.getAggregatedFeatureType());
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        });
    }

    private List<AdeAggregationRecord> generateUserFeatureAggrRecord(Map<String, String> context, String featureName, double featureValue, int amountOfRecords, Instant startInstant, Instant endInstant) {
        List<AdeAggregationRecord > records = new LinkedList<>();
        for( int i=0; i< amountOfRecords ; i++)
        {
            AdeAggregationRecord record = new ScoredFeatureAggregationRecord(50D,null,startInstant,endInstant, featureName, featureValue, "featureBucketConfName",context, FEATURE_AGGREGATION);
            records.add(record);
        }
        return records;
    }

    private List<AdeAggregationRecord > generateUserScoreAggrRecord(Map<String, String> context, String featureName, double featureValue, int amountOfRecords, Instant startInstant, Instant endInstant) {
        List<AdeAggregationRecord > records = new LinkedList<>();
        for( int i=0; i< amountOfRecords ; i++)
        {
            AdeAggregationRecord record = new AdeAggregationRecord(startInstant,endInstant, featureName, featureValue, "featureBucketConfName",context, SCORE_AGGREGATION);
            records.add(record);
        }
        return records;
    }

    @Test
    public void getContextIdToNumOfItemsList() throws Exception {
        TimeRange timeRange = new TimeRange(Instant.EPOCH, Instant.now());

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = paginationService.getContextIdToNumOfItemsList(aggregatedDataPaginationParamSet, timeRange);
        Assert.assertEquals(contextIdToNumOfItemsList.size(), AMOUNT_OF_GENERATED_CONTEXTS);
        contextIdToNumOfItemsList.forEach(contextIdToNumOfItems -> Assert.assertEquals(contextIdToNumOfItems.getTotalNumOfItems(),1));
    }

    @Test
    public void getPageIterator() throws Exception {
        TimeRange timeRange = new TimeRange(startInstant, endInstant);
        List<PageIterator<AdeAggregationRecord>> pageIterators = paginationService.getPageIterators(aggregatedDataPaginationParamSet, timeRange);
        Assert.assertEquals("pagination service should contain multiple page iterators",2,pageIterators.size());
        pageIterators.forEach(pageIterator -> {
            List<AdeAggregationRecord> pageRecords = pageIterator.next();
            Assert.assertTrue("page must not be empty",pageRecords.size()>0);
            boolean hasNext = pageIterator.hasNext();
            Assert.assertFalse("pageIterator must contain exactly one page",hasNext);
        });
    }

}