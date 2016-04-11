package fortscale.services.monitoring.stats.engine;

/**
 *
 * A POJO class that holds long metric data. It is used at StatsEngineMetricsGroupData.
 *
 * See  It is used at StatsEngineMetricsGroupData.
 *
 * Created by gaashh on 4/6/16.
 */

public class StatsEngineLongMetricData {

    protected String name;
    protected long value;

    // ctor
    public StatsEngineLongMetricData(String name, long value) {
        this.name = name;
        this.value = value;
    }

    // --- getters/setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}