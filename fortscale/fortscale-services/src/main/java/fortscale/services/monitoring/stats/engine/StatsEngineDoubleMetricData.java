package fortscale.services.monitoring.stats.engine;

/**
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
