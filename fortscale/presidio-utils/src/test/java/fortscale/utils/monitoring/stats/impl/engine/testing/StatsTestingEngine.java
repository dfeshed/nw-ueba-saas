package fortscale.utils.monitoring.stats.impl.engine.testing;

import fortscale.utils.monitoring.stats.engine.StatsEngineBase;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.junit.Ignore;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gaashh on 4/6/16.
 */
@Ignore
public class StatsTestingEngine extends StatsEngineBase {

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

    @Override
    protected void preDestroy() {

    }

    public void flushMetricsGroupData() {
        // NOP
    }

    // Might return null
    public StatsEngineMetricsGroupData getLatestMetricsGroupData(String groupName) {

        StatsEngineMetricsGroupData data =  latestMetricsGroupData.get(groupName);

        return data;
    }

    public String statsEngineDataToJsonInString(List<StatsEngineMetricsGroupData> statsEngineDataList) {

        EngineData engineData = statsEngineDataToModelData(statsEngineDataList);

        String jsonInString = modelMetricGroupToJsonInString(engineData);

        return jsonInString;
    }



}
