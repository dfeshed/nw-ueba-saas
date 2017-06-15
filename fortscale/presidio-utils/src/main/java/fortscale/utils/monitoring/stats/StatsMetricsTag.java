package fortscale.utils.monitoring.stats;

import java.util.List;

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

    /**
     * A copy ctor
     *
     * @param other
     */
    public StatsMetricsTag(StatsMetricsTag other) {
        this.name  = other.name;
        this.value = other.value;
    }

    /**
     *
     * An helper function to convert a tag list to a string in the format "tag1=value1 tag2=value2 ..."
     *
     * @param tagsList  - tag list to convert
     * @return          - result string
     */
    static public String metricsTagListToString(List<StatsMetricsTag> tagsList) {

        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (StatsMetricsTag tag : tagsList) {

            // Add seperating space unless at the first time
            if (first) {
                first = false;
            }
            else {
                result.append(" ");
            }
            // Add the tag
            result.append( String.format("%s=%s", tag.getName(), tag.getValue()) );

        }

        return result.toString();
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
