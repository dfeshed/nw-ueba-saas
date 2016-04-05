package fortscale.services.monitoring.stats;

/**
 * Created by gaashh on 4/3/16.
 */


// A POJO Class to hold metric tag
public class StatsMetricsTag {

    private String name;
    private String value;

    public StatsMetricsTag(String tagName, String tagValue) {
        name  = tagName;
        value = tagValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
