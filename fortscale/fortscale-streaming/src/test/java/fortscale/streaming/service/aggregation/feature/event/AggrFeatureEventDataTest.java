package fortscale.streaming.service.aggregation.feature.event;

import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by amira on 15/07/2015.
 */
public class AggrFeatureEventDataTest {

    @Test(expected = Exception.class)
    public void testNewEventData_nullBuilder() {
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventData eventData = new AggrFeatureEventData(null, context, 1);
    }

    @Test(expected = Exception.class)
    public void testNewEventData_nullContext() {
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, null, 1);
    }

    @Test(expected = Exception.class)
    public void testNewEventData_negativeBucketLeap() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT

        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, -1);
    }

    @Test
    public void testAddBucketID() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

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
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

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
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

        String startegyName = "dailyStrategy";
        String strategyContextId = "dailyStrategy_"+startTime1;

        FeatureBucketStrategyData strategyData = new FeatureBucketStrategyData(strategyContextId, startegyName, startTime1, endTime1);
        String strategyId = strategyData.getStrategyId();
        String bucketID1 = strategyId+"username_john_machine_m1";
        String bucketID2 = "bucketID2";
        String bucketID3 = strategyId+"username_john_machine_m1";

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

    @Test(expected = Exception.class)
    public void testAddBucketID_existingStrategy_wrongTime() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        context.put("username", "john");
        context.put("machine", "m1");
        AggrFeatureEventBuilder builder = mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

        String startegyName = "dailyStrategy";
        String strategyContextId = "dailyStrategy_"+startTime1;

        FeatureBucketStrategyData strategyData = new FeatureBucketStrategyData(strategyContextId, startegyName, startTime1, endTime1);
        String strategyId = strategyData.getStrategyId();
        String bucketID3 = strategyId+"username_john_machine_m1";

        eventData.nextBucketEndTimeUpdate(strategyData);

        // bucketID3 should not be added because the times are not matching those of the strategy
        eventData.addBucketID(bucketID3, startTime2, endTime2);

     }

    @Test(expected = Exception.class)
    public void testAddBucketID_bucketIdNotMatchStrategyID() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        context.put("username", "john");
        context.put("machine", "m1");
        AggrFeatureEventBuilder builder = mock(AggrFeatureEventBuilder.class);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

        String startegyName = "dailyStrategy";
        String strategyContextId = "dailyStrategy_"+startTime1;

        FeatureBucketStrategyData strategyData = new FeatureBucketStrategyData(strategyContextId, startegyName, startTime1, endTime1);
        String strategyId = strategyData.getStrategyId();
        String bucketID2 = "bucketID2";

        eventData.nextBucketEndTimeUpdate(strategyData);

        // bucketID2 should not be added because the bucketID not match the strategyID of the last bucket in the list.
        eventData.addBucketID(bucketID2, startTime2, endTime2);
    }

    @Test
    public void testSetEndTime() {
        Long startTime1 = 1436918400L; //Wed, 15 Jul 2015 00:00:00 GMT
        Long endTime1 = 1437004799L; //Wed, 15 Jul 2015 23:59:59 GMT
        Long startTime2 = 1437004800L; //Thu, 16 Jul 2015 00:00:00 GMT
        Long endTime2 = 1437091199L; //Thu, 16 Jul 2015 23:59:59 GMT
        Long endTime3 = 1437177599L; // Fri, 17 Jul 2015 23:59:59 GMT
        Map<String, String> context = new HashMap<>();
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

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
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

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
        AggrFeatureEventBuilder builder = new AggrFeatureEventBuilder(null, null, null);
        AggrFeatureEventData eventData = new AggrFeatureEventData(builder, context, 1);

        String bucketID1 = "bucketID1";
        String bucketID2 = "bucketID2";

        eventData.addBucketID(bucketID1, startTime1, endTime1);
        eventData.setEndTime(bucketID2, endTime2);
    }

}