package fortscale.aggregation.feature.bucket.strategy;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.common.event.Event;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by amira on 20/10/2015.
 */
public class FixedDurationFeatureBucketStrategyTest {

    static FixedDurationFeatureBucketStrategy fixedDurationFeatureBucketStrategy = new FixedDurationFeatureBucketStrategy("hourly", 3600);
    static long EPOCHTIME_2015_08_31_23_59_59 = 1441065599;
    static long EPOCHTIME_2015_09_01_00_00_00 = 1441065600;
    static long EPOCHTIME_2015_09_01_00_59_59 = 1441069199;
    static long EPOCHTIME_2015_09_01_01_00_00 = 1441069200;
    static FeatureBucketStrategyData strategyData = null;

    @Test
    public void getFeatureBucketStrategyDataTest() {
        FeatureBucketConf bucketConf = null;
        Event event = null;
        List<FeatureBucketStrategyData> featureBucketStrategyDatas = fixedDurationFeatureBucketStrategy.getFeatureBucketStrategyData(bucketConf, event, EPOCHTIME_2015_09_01_00_00_00);
        Assert.assertEquals(1, featureBucketStrategyDatas.size());

        FeatureBucketStrategyData featureBucketStrategyData = featureBucketStrategyDatas.get(0);
        /*
        System.out.println(featureBucketStrategyData.getStartTime());
        System.out.println(featureBucketStrategyData.getEndTime());
        System.out.println(featureBucketStrategyData.getContextMap().toString());
        System.out.println(featureBucketStrategyData.getStrategyEventContextId());
        System.out.println(featureBucketStrategyData.getStrategyId());
        System.out.println(featureBucketStrategyData.getStrategyName());
        */

        Assert.assertEquals(EPOCHTIME_2015_09_01_00_00_00, featureBucketStrategyData.getStartTime());
        Assert.assertEquals(EPOCHTIME_2015_09_01_00_59_59, featureBucketStrategyData.getEndTime());
        Assert.assertEquals("{}", featureBucketStrategyData.getContextMap().toString());
        Assert.assertEquals("hourly", featureBucketStrategyData.getStrategyEventContextId());
        Assert.assertEquals("hourly_1441065600", featureBucketStrategyData.getStrategyId());
        Assert.assertEquals("hourly", featureBucketStrategyData.getStrategyName());

    }

    @Test
    public void  getNextBucketStrategyDataTest1() {
        FeatureBucketStrategyData featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_08_31_23_59_59);
        Assert.assertEquals(EPOCHTIME_2015_09_01_00_00_00, featureBucketStrategyData.getStartTime());
     }

    @Test
    public void  getNextBucketStrategyDataTest2() {
        FeatureBucketStrategyData featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+1);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+2);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+3);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+4);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+5);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+6);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+7);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+8);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+9);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
        featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_00_00+3598);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
    }


    @Test
    public void  getNextBucketStrategyDataTest3() {
        FeatureBucketStrategyData featureBucketStrategyData = fixedDurationFeatureBucketStrategy.getNextBucketStrategyData(null, null, EPOCHTIME_2015_09_01_00_59_59);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, featureBucketStrategyData.getStartTime());
    }



    @Test
    public void notifyWhenNextBucketEndTimeIsKnownTest() {

        NextBucketEndTimeListener listener = new NextBucketEndTimeListener() {
            @Override
            public void nextBucketEndTimeUpdate(FeatureBucketStrategyData sd) {
                strategyData = sd;
            }
        };

        fixedDurationFeatureBucketStrategy.notifyWhenNextBucketEndTimeIsKnown(null, null, listener, EPOCHTIME_2015_09_01_00_59_59);
        Assert.assertEquals(EPOCHTIME_2015_09_01_01_00_00, strategyData.getStartTime());
    }

    @Test
    public void getStrategyContextIdFromStrategyIdTest() {
       Assert.assertEquals("hourly", fixedDurationFeatureBucketStrategy.getStrategyContextIdFromStrategyId("what ever, it dosn't matter"));
    }
}



