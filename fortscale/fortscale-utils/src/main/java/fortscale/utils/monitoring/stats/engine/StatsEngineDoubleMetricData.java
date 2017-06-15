package fortscale.utils.monitoring.stats.engine;

/**
 *
 * A POJO class that holds double metric data. It is used at StatsEngineMetricsGroupData.
 *
 * See  It is used at StatsEngineMetricsGroupData.
 *
 * Created by gaashh on 4/6/16.
 */

public class StatsEngineDoubleMetricData {

    protected String name;
    protected double value;

    // ctor
    public StatsEngineDoubleMetricData(String name, double value) {
        this.name  = name;
        this.value = value;
    }

    // --- getters/setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
