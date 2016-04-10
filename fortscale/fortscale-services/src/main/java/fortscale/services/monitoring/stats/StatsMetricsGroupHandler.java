package fortscale.services.monitoring.stats;

/**
 * Created by gaashh on 4/3/16.
 */
public interface StatsMetricsGroupHandler {

    public void manualUpdate();
    public void manualUpdate(long epochTime);
    public void writeMetricGroupsToEngine(long epochTime);

}