package fortscale.services.monitoring.stats.engine;

/**
 *
 * A POJO class that holds string metric data. It is used at StatsEngineMetricsGroupData.
 *
 * See  It is used at StatsEngineMetricsGroupData.
 *
 * Created by gaashh on 4/6/16.
 */
public class StatsEngineStringMetricData {

    protected String name;
    protected String value;

    public StatsEngineStringMetricData(String name, String value) {
        this.name  = name;
        this.value = value;
    }

    // --- getters/setters ---

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
