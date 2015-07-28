package fortscale.aggregation.feature.event;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategy;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by amira on 15/07/2015.
 */
public class AggrFeatureEventDataTest {
    @Mock
    private DataSourcesSyncTimer dataSourcesSyncTimer;

    @Mock
    private FeatureBucketsService featureBucketsService;

    @Mock
    private AggrEventTopologyService aggrEventTopologyService;

    @Test(expected = Exception.class)
    public void testNewEventData_nullBuilder() {
        Map<String, String> context = new HashMap<>();
        @SuppressWarnings("unused")
        AggrFeatureEventData eventData = new AggrFeatureEventData(null, context, "strategyId");
    }

    @Test(expected = Exception.class)
    public void testNewEventData_nullContext() {
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);
        @SuppressWarnings("unused")
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, null, "strategyId");
    }

    @Test
    public void testAddBucketID() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);

        List<AggrFeatureEventData.BucketData> bucketIDs = eventData.getBucketIDs();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketData bucketData = bucketIDs.get(0);
        Assert.assertEquals(null, bucketData.getStrategyData());
        Assert.assertEquals(bucketID1, bucketData.getBucketID());
        Assert.assertEquals(startTime1, bucketData.getStartTime());
        Assert.assertEquals(endTime1, bucketData.getEndTime());

        bucketData = bucketIDs.get(1);
        Assert.assertEquals(null, bucketData.getStrategyData());
        Assert.assertEquals(bucketID2, bucketData.getBucketID());
        Assert.assertEquals(startTime2, bucketData.getStartTime());
        Assert.assertEquals(endTime2, bucketData.getEndTime());
    }

    @Test(expected = Exception.class)
    public void testAddBucketID_wrongOrder() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID1, startTime1, endTime1);
    }

    @Test
    public void testAddBucketID_existingStrategy() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        context.put("username", "john");
        context.put("machine", "m1");
        AggrFeatureEventBuilder builder = mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String startegyName = "dailyStrategy";
        String strategyContextId = "dailyStrategy_"+startTime1;

        FeatureBucketStrategyData strategyData = new FeatureBucketStrategyData(strategyContextId, startegyName, startTime1, endTime1);
        String strategyId = strategyData.getStrategyId();
        String bucketID1 = strategyId+"username_john_machine_m1";
        String bucketID2 = "bucketID2";

        eventData.nextBucketEndTimeUpdate(strategyData);

        // bucketID1 matches the strategyData already added
        eventData.addBucketID(bucketID1, startTime1, endTime1);

        // bucketID2 should be second
        eventData.addBucketID(bucketID2, startTime2, endTime2);

        List<AggrFeatureEventData.BucketData> bucketIDs = eventData.getBucketIDs();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketData bucketData = bucketIDs.get(0);
        Assert.assertEquals(strategyData, bucketData.getStrategyData());
        Assert.assertEquals(bucketID1, bucketData.getBucketID());
        Assert.assertEquals(startTime1, bucketData.getStartTime());
        Assert.assertEquals(endTime1, bucketData.getEndTime());

        bucketData = bucketIDs.get(1);
        Assert.assertEquals(null, bucketData.getStrategyData());
        Assert.assertEquals(bucketID2, bucketData.getBucketID());
        Assert.assertEquals(startTime2, bucketData.getStartTime());
        Assert.assertEquals(endTime2, bucketData.getEndTime());
    }



    @Test
    public void testSetEndTime() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Long endTime3 = 1437177599L; // Fri, 17 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder = mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.setEndTime(bucketID2, endTime3);

        List<AggrFeatureEventData.BucketData> bucketIDs = eventData.getBucketIDs();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketData bucketData = bucketIDs.get(0);
        Assert.assertEquals(null, bucketData.getStrategyData());
        Assert.assertEquals(bucketID1, bucketData.getBucketID());
        Assert.assertEquals(startTime1, bucketData.getStartTime());
        Assert.assertEquals(endTime1, bucketData.getEndTime());

        bucketData = bucketIDs.get(1);
        Assert.assertEquals(null, bucketData.getStrategyData());
        Assert.assertEquals(bucketID2, bucketData.getBucketID());
        Assert.assertEquals(startTime2, bucketData.getStartTime());
        Assert.assertEquals(endTime3, bucketData.getEndTime());
    }

    @Test(expected = Exception.class)
    public void testSetEndTime_notToLatestBucketID() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Long endTime3 = 1437177599L; // Fri, 17 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.setEndTime(bucketID1, endTime3);
    }

    @Test(expected = Exception.class)
    public void testSetEndTime_BucketIdNotExist() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);// 
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.setEndTime(bucketID2, endTime2);
    }

    private AggrFeatureEventBuilder createBuilder(int numberOfBuckets, int bucketLeap) {
        // Creating AggregatedFeatureEventConf
        Map<String, List<String>> paramters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("letters");
        paramters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_number_of_distinct_values_func");
        funcJSONObj.put("includeValues", true);

        AggregatedFeatureEventConf eventConf = new AggregatedFeatureEventConf("my_number_of_distinct_values", "bc1", numberOfBuckets , bucketLeap, 0, paramters2featuresListMap, funcJSONObj );
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        eventConf.setBucketConf(bucketConf);


        FeatureBucketStrategy strategy = mock(FeatureBucketStrategy.class);

        AggrFeatureEventService aggrFeatureEventService = mock(AggrFeatureEventService.class);

        // Create AggrFeatureEventBuilder
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(eventConf, strategy, aggrFeatureEventService, featureBucketsService);


        builder.setAggrEventTopologyService(aggrEventTopologyService);
        builder.setDataSourcesSyncTimer(dataSourcesSyncTimer);
        builder.setFeatureBucketsService(featureBucketsService);

        return builder;
    }

    @Test
    public void testMatchBucketLeap1() {
        Long startTime1 = 1L;
        Long endTime1 = 2L;
        Long startTime2 = 3L;
        Long endTime2 = 4L;
        Long startTime3 = 5L;
        Long endTime3 = 6L;
        Long startTime4 = 7L;
        Long endTime4 = 8L;
        Long startTime5 = 9L;
        Long endTime5 = 10L;
        Long startTime6 = 11L;
        Long endTime6 = 12L;
        Long startTime7 = 13L;
        Long endTime7 = 14L;
        Long startTime8 = 15L;
        Long endTime8 = 16L;
        Long startTime9 = 17L;
        Long endTime9 = 18L;
        Long startTime10 = 19L;
        Long endTime10 = 20L;

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";
        String bucketID3 = "bucketID3";
        String bucketID4 = "bucketID4";
        String bucketID5 = "bucketID5";
        String bucketID6 = "bucketID6";
        String bucketID7 = "bucketID7";
        String bucketID8 = "bucketID8";
        String bucketID9 = "bucketID9";
        String bucketID10 = "bucketID10";

        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 1);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID3, startTime3, endTime3);
        eventData.addBucketID(bucketID4, startTime4, endTime4);
        eventData.addBucketID(bucketID5, startTime5, endTime5);
        eventData.addBucketID(bucketID6, startTime6, endTime6);
        eventData.addBucketID(bucketID7, startTime7, endTime7);
        eventData.addBucketID(bucketID8, startTime8, endTime8);
        eventData.addBucketID(bucketID9, startTime9, endTime9);
        eventData.addBucketID(bucketID10, startTime10, endTime10);

        for(int i=0; i<10; i++){
            Assert.assertTrue(eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketData(eventData.getBucketIDs().get(j));
            for(int i=0; i<10; i++){
                Assert.assertTrue(eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
            }
        }

    }

    @Test
    public void testMatchBucketLeap2() {
        Long startTime1 = 1L;
        Long endTime1 = 2L;
        Long startTime2 = 3L;
        Long endTime2 = 4L;
        Long startTime3 = 5L;
        Long endTime3 = 6L;
        Long startTime4 = 7L;
        Long endTime4 = 8L;
        Long startTime5 = 9L;
        Long endTime5 = 10L;
        Long startTime6 = 11L;
        Long endTime6 = 12L;
        Long startTime7 = 13L;
        Long endTime7 = 14L;
        Long startTime8 = 15L;
        Long endTime8 = 16L;
        Long startTime9 = 17L;
        Long endTime9 = 18L;
        Long startTime10 = 19L;
        Long endTime10 = 20L;

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";
        String bucketID3 = "bucketID3";
        String bucketID4 = "bucketID4";
        String bucketID5 = "bucketID5";
        String bucketID6 = "bucketID6";
        String bucketID7 = "bucketID7";
        String bucketID8 = "bucketID8";
        String bucketID9 = "bucketID9";
        String bucketID10 = "bucketID10";

        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 2);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID3, startTime3, endTime3);
        eventData.addBucketID(bucketID4, startTime4, endTime4);
        eventData.addBucketID(bucketID5, startTime5, endTime5);
        eventData.addBucketID(bucketID6, startTime6, endTime6);
        eventData.addBucketID(bucketID7, startTime7, endTime7);
        eventData.addBucketID(bucketID8, startTime8, endTime8);
        eventData.addBucketID(bucketID9, startTime9, endTime9);
        eventData.addBucketID(bucketID10, startTime10, endTime10);

        for(int i=0; i<10; i++){
            Assert.assertEquals((i % 2 == 1), eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketData(eventData.getBucketIDs().get(j));
            for(int i=0; i<10; i++){
                Assert.assertEquals((i % 2 == j % 2), eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
            }
        }

    }

    @Test
    public void testMatchBucketLeap3() {
        Long startTime1 = 1L;
        Long endTime1 = 2L;
        Long startTime2 = 3L;
        Long endTime2 = 4L;
        Long startTime3 = 5L;
        Long endTime3 = 6L;
        Long startTime4 = 7L;
        Long endTime4 = 8L;
        Long startTime5 = 9L;
        Long endTime5 = 10L;
        Long startTime6 = 11L;
        Long endTime6 = 12L;
        Long startTime7 = 13L;
        Long endTime7 = 14L;
        Long startTime8 = 15L;
        Long endTime8 = 16L;
        Long startTime9 = 17L;
        Long endTime9 = 18L;
        Long startTime10 = 19L;
        Long endTime10 = 20L;

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";
        String bucketID3 = "bucketID3";
        String bucketID4 = "bucketID4";
        String bucketID5 = "bucketID5";
        String bucketID6 = "bucketID6";
        String bucketID7 = "bucketID7";
        String bucketID8 = "bucketID8";
        String bucketID9 = "bucketID9";
        String bucketID10 = "bucketID10";

        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 3);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID3, startTime3, endTime3);
        eventData.addBucketID(bucketID4, startTime4, endTime4);
        eventData.addBucketID(bucketID5, startTime5, endTime5);
        eventData.addBucketID(bucketID6, startTime6, endTime6);
        eventData.addBucketID(bucketID7, startTime7, endTime7);
        eventData.addBucketID(bucketID8, startTime8, endTime8);
        eventData.addBucketID(bucketID9, startTime9, endTime9);
        eventData.addBucketID(bucketID10, startTime10, endTime10);

        for(int i=0; i<10; i++){
            Assert.assertEquals((i % 3 == 2), eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketData(eventData.getBucketIDs().get(j));
            for(int i=0; i<10; i++){
                Assert.assertEquals((i % 3 == j % 3), eventData.doesItMatchBucketLeap(eventData.getBucketIDs().get(i)));
            }
        }
    }

     @Test
    public void testCleanOldBuckets1() {
        Long startTime1 = 1L;
        Long endTime1 = 2L;
        Long startTime2 = 3L;
        Long endTime2 = 4L;
        Long startTime3 = 5L;
        Long endTime3 = 6L;
        Long startTime4 = 7L;
        Long endTime4 = 8L;
        Long startTime5 = 9L;
        Long endTime5 = 10L;
        Long startTime6 = 11L;
        Long endTime6 = 12L;
        Long startTime7 = 13L;
        Long endTime7 = 14L;
        Long startTime8 = 15L;
        Long endTime8 = 16L;
        Long startTime9 = 17L;
        Long endTime9 = 18L;
        Long startTime10 = 19L;
        Long endTime10 = 20L;

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";
        String bucketID3 = "bucketID3";
        String bucketID4 = "bucketID4";
        String bucketID5 = "bucketID5";
        String bucketID6 = "bucketID6";
        String bucketID7 = "bucketID7";
        String bucketID8 = "bucketID8";
        String bucketID9 = "bucketID9";
        String bucketID10 = "bucketID10";

        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 2);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID3, startTime3, endTime3);
        eventData.addBucketID(bucketID4, startTime4, endTime4);
        eventData.addBucketID(bucketID5, startTime5, endTime5);
        eventData.addBucketID(bucketID6, startTime6, endTime6);
        eventData.addBucketID(bucketID7, startTime7, endTime7);
        eventData.addBucketID(bucketID8, startTime8, endTime8);
        eventData.addBucketID(bucketID9, startTime9, endTime9);
        eventData.addBucketID(bucketID10, startTime10, endTime10);

        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(9).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(5).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(3).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(1).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(4, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(1).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(3, eventData.getBucketIDs().size());

    }

    @Test
    public void testCleanOldBuckets2() {
        Long startTime1 = 1L;
        Long endTime1 = 2L;
        Long startTime2 = 3L;
        Long endTime2 = 4L;
        Long startTime3 = 5L;
        Long endTime3 = 6L;
        Long startTime4 = 7L;
        Long endTime4 = 8L;
        Long startTime5 = 9L;
        Long endTime5 = 10L;
        Long startTime6 = 11L;
        Long endTime6 = 12L;
        Long startTime7 = 13L;
        Long endTime7 = 14L;
        Long startTime8 = 15L;
        Long endTime8 = 16L;
        Long startTime9 = 17L;
        Long endTime9 = 18L;
        Long startTime10 = 19L;
        Long endTime10 = 20L;

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";
        String bucketID3 = "bucketID3";
        String bucketID4 = "bucketID4";
        String bucketID5 = "bucketID5";
        String bucketID6 = "bucketID6";
        String bucketID7 = "bucketID7";
        String bucketID8 = "bucketID8";
        String bucketID9 = "bucketID9";
        String bucketID10 = "bucketID10";

        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 3);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.addBucketID(bucketID3, startTime3, endTime3);
        eventData.addBucketID(bucketID4, startTime4, endTime4);
        eventData.addBucketID(bucketID5, startTime5, endTime5);
        eventData.addBucketID(bucketID6, startTime6, endTime6);
        eventData.addBucketID(bucketID7, startTime7, endTime7);
        eventData.addBucketID(bucketID8, startTime8, endTime8);
        eventData.addBucketID(bucketID9, startTime9, endTime9);
        eventData.addBucketID(bucketID10, startTime10, endTime10);

        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(8).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(5).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketIDs().size());

        eventData.getBucketIDs().get(2).setWasSentAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(3, eventData.getBucketIDs().size());

    }


}