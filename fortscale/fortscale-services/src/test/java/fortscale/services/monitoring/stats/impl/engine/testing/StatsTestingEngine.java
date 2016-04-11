package fortscale.services.monitoring.stats.impl.engine.testing;

import fortscale.services.monitoring.stats.engine.StatsEngine;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;
import org.jcodings.util.Hash;

import java.util.HashMap;

/**
 * Created by gaashh on 4/6/16.
 */
public class StatsTestingEngine implements StatsEngine {

    HashMap<String,StatsEngineMetricsGroupData> latestMetricsGroupData;

    // ctor
    public StatsTestingEngine() {
        latestMetricsGroupData = new HashMap<>();
    }

    public void writeMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {

        String groupName = metricsGroupData.getGroupName();

        latestMetricsGroupData.put(groupName, metricsGroupData);

        //System.out.println(this.getClass().getName() + ": write \n" + metricsGroupData.toString());
    }

    // Might return null
    public StatsEngineMetricsGroupData getLatestMetricsGroupData(String groupName) {

        StatsEngineMetricsGroupData data =  latestMetricsGroupData.get(groupName);

        return data;
    }



}
