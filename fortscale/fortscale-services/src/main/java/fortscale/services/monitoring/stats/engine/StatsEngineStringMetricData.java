package fortscale.services.monitoring.stats.engine;

/**
 * Created by gaashh on 4/6/16.
 */
public class StatsEngineStringMetricData {

    protected String name;
    protected String value;

    // ctor
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
