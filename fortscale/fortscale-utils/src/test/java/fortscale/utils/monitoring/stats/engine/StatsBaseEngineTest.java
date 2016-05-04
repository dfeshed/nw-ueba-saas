package fortscale.utils.monitoring.stats.engine;

import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaashh on 4/17/16.
 */
public class StatsBaseEngineTest {
    

    @Test
    public void testModelMetricGroupToJsonInString() throws Exception {

        long setIndex = 3;
        List<StatsEngineMetricsGroupData> metricGroupDataList = StatsEngineTestingUtils.createdStatsMetricsGroupsList(setIndex);
        String expected = StatsEngineTestingUtils.getExpectedMetricsGroupListJsonString(setIndex);

        StatsTestingEngine engine = new StatsTestingEngine();

        String result = engine.statsEngineDataToJsonInString(metricGroupDataList);

        Assert.assertEquals(expected,result);
    }

}