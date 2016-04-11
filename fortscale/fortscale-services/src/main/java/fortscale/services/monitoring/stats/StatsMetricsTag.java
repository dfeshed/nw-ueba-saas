package fortscale.services.monitoring.stats;

/**
 *
 * An helper class to StatsMetricsGroupAttributes. It holds metrics group tag. The tag has name and value.
 *
 * Created by gaashh on 4/3/16.
 */

public class StatsMetricsTag {

    private String name;
    private String value;

    /**
     *
     * Init the tag with its name and value
     *
     * @param tagName
     * @param tagValue
     */
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
