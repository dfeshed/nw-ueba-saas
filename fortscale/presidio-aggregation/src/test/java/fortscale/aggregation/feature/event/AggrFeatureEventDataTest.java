package fortscale.aggregation.feature.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortscale.aggregation.DataSourcesSyncTimer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategy;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.bucket.strategy.FixedDurationFeatureBucketStrategyFactory;
import fortscale.aggregation.feature.bucket.strategy.StrategyJson;
import net.minidev.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/aggr-feature-event-builder-context.xml"})
public class AggrFeatureEventDataTest {

    @MockBean
    DataSourcesSyncTimer dataSourcesSyncTimer;

    @MockBean
    AggrEventTopologyService aggrEventTopologyService;

    long startTime1 = 1420070400L; //01 Jan 2015 00:00:00 GMT
    long endTime1 = 1420156799L; //01 Jan 2015 23:59:59 GMT
    long startTime2 = 1420156800L; //02 Jan 2015 00:00:00 GMT
    long endTime2 = 1420243199L; //02 Jan 2015 23:59:59 GMT
    long startTime3 = 1420243200L; //03 Jan 2015 00:00:00 GMT
    long endTime3 = 1420329599L; // 03 Jan 2015 23:59:59 GMT
    long startTime4 = 1420329600L;
    long endTime4 = 1420415999L;
    long startTime5 = 1420416000L;
    long endTime5 = 1420502399L;
    long startTime6 = 1420502400L;
    long endTime6 = 1420588799L;
    long startTime7 = 1420588800L;
    long endTime7 = 1420675199L;
    long startTime8 = 1420675200L;
    long endTime8 = 1420761599L;
    long startTime9 = 1420761600L;
    long endTime9 = 1420847999L;
    long startTime10 = 1420848000L;
    long endTime10 = 1420934399L;

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
    

    FeatureBucketStrategy strategy;
    
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
    public void testAddBucketID() throws Exception{
        long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  createBuilder(3, 1);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);

        List<AggrFeatureEventData.BucketTick> bucketIDs = eventData.getBucketTicks();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketTick bucketTick = bucketIDs.get(0);
        Assert.assertEquals(null, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID1, bucketTick.getBucketId());
        Assert.assertEquals(startTime1, bucketTick.getStartTime());
        Assert.assertEquals(endTime1, bucketTick.getEndTime());

        bucketTick = bucketIDs.get(1);
        Assert.assertEquals(null, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID2, bucketTick.getBucketId());
        Assert.assertEquals(startTime2, bucketTick.getStartTime());
        Assert.assertEquals(endTime2, bucketTick.getEndTime());
    }

    @Test
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
        Assert.assertEquals(bucketID1, eventData.getBucketTicks().get(0).getBucketId());
        Assert.assertEquals(bucketID2, eventData.getBucketTicks().get(1).getBucketId());
    }

    @Test
    public void testAddBucketID_existingStrategy() throws Exception{
        long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        context.put("username", "john");
        context.put("machine", "m1");
        AggrFeatureEventBuilder builder = createBuilder(3, 1);
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

        List<AggrFeatureEventData.BucketTick> bucketIDs = eventData.getBucketTicks();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketTick bucketTick = bucketIDs.get(0);
        Assert.assertEquals(strategyData, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID1, bucketTick.getBucketId());
        Assert.assertEquals(startTime1, bucketTick.getStartTime());
        Assert.assertEquals(endTime1, bucketTick.getEndTime());

        bucketTick = bucketIDs.get(1);
        Assert.assertEquals(null, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID2, bucketTick.getBucketId());
        Assert.assertEquals(startTime2, bucketTick.getStartTime());
        Assert.assertEquals(endTime2, bucketTick.getEndTime());
    }



    @Test
    public void testSetEndTime() throws Exception{
        long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        long endTime3 = 1437177599L; // Fri, 17 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder = createBuilder(3, 1);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.addBucketID(bucketID2, startTime2, endTime2);
        eventData.setEndTime(bucketID2, endTime3);

        List<AggrFeatureEventData.BucketTick> bucketIDs = eventData.getBucketTicks();

        Assert.assertEquals(2, bucketIDs.size());
        AggrFeatureEventData.BucketTick bucketTick = bucketIDs.get(0);
        Assert.assertEquals(null, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID1, bucketTick.getBucketId());
        Assert.assertEquals(startTime1, bucketTick.getStartTime());
        Assert.assertEquals(endTime1, bucketTick.getEndTime());

        bucketTick = bucketIDs.get(1);
        Assert.assertEquals(null, bucketTick.getStrategyData());
        Assert.assertEquals(bucketID2, bucketTick.getBucketId());
        Assert.assertEquals(startTime2, bucketTick.getStartTime());
        Assert.assertEquals(endTime3, bucketTick.getEndTime());
    }

    @Test(expected = Exception.class)
    public void testSetEndTime_notToLatestBucketID() {
        long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        long endTime3 = 1437177599L; // Fri, 17 Jul 2015 23:59:59 GMT
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
        long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder =  mock(AggrFeatureEventBuilder.class);// 
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, "strategyId");

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.setEndTime(bucketID2, endTime2);
    }



    @Test
    public void testMatchBucketLeap1() throws Exception{

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
            Assert.assertTrue(eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketTick(eventData.getBucketTicks().get(j));
            for(int i=0; i<10; i++){
                Assert.assertTrue(eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
            }
        }

    }

    @Test
    public void testMatchBucketLeap2() throws Exception{

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
            Assert.assertEquals((i % 2 == 1), eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketTick(eventData.getBucketTicks().get(j));
            for(int i=0; i<10; i++){
                Assert.assertEquals((i % 2 == j % 2), eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
            }
        }

    }

    @Test
    public void testMatchBucketLeap3() throws Exception {

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
            Assert.assertEquals((i % 3 == 2), eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
        }

        for(int j=0; j<10; j++) {
            eventData.setLastSentEventBucketTick(eventData.getBucketTicks().get(j));
            for(int i=0; i<10; i++){
                Assert.assertEquals((i % 3 == j % 3), eventData.doesItMatchBucketLeap(eventData.getBucketTicks().get(i)));
            }
        }
    }

     @Test
    public void testCleanOldBuckets1() throws Exception{
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
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(9).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(5).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(3).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(1).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(4, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(1).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(4, eventData.getBucketTicks().size());

    }

    @Test
    public void testCleanOldBuckets2() throws Exception{

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
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(8).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(5).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(10, eventData.getBucketTicks().size());

        eventData.getBucketTicks().get(2).setProcessedAsLeadingBucket(true);
        eventData.clearOldBucketData();
        Assert.assertEquals(4, eventData.getBucketTicks().size());

    }

    private AggrFeatureEventBuilder createBuilder(int numberOfBuckets, int bucketLeap) throws Exception{
        // Creating AggregatedFeatureEventConf
        Map<String, List<String>> paramters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("letters");
        paramters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_distinct_values_counter_func");
        funcJSONObj.put("includeValues", true);

        AggregatedFeatureEventConf eventConf = new AggregatedFeatureEventConf("my_number_of_distinct_values", "F", "bc1", numberOfBuckets , bucketLeap, 0, "HighestScore", paramters2featuresListMap, funcJSONObj );
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        eventConf.setBucketConf(bucketConf);


        strategy = createFixedDurationStrategy();

        // Create AggrFeatureEventBuilder
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(eventConf, strategy, mock(FeatureBucketsService.class));

        return builder;
    }

    private FeatureBucketStrategy createFixedDurationStrategy() throws Exception{
        JSONObject strategyJson = new JSONObject();
        strategyJson.put("name", "fixed_time_daily");
        strategyJson.put("type", "fixed_time");
        JSONObject params = new JSONObject();
        params.put("durationInSeconds", 60*60*24);
        strategyJson.put("params", params);

        return new FixedDurationFeatureBucketStrategyFactory().createFeatureBucketStrategy(new StrategyJson(strategyJson));
    }

}