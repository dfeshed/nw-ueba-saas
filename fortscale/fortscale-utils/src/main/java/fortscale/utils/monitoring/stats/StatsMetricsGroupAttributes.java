package fortscale.utils.monitoring.stats;

import java.util.LinkedList;
import java.util.List;

/**
 * This class holds a metrics group attributes. It is a parameter to the StatsMetricsGroup ctor.
 *
 * IT holds:
 * 1. Group name - the metrics group name (measurement name). Note that group name annotation has priority over this
 * 2. Tag list - a list of tags names and tag values to be attached to the group
 *
 * Created by gaashh on 4/5/16.
 */

// TODO: Add tag validations
// TODO: Add measurement name validation
// TODO: Add name
public class StatsMetricsGroupAttributes {

    // The metrics group name (measurement name). Note that group name annotation has priority over this
    protected String                groupName;

    // Tag list - a list of tags names and tag values to be attached to the group
    protected List<StatsMetricsTag> metricsTags = new LinkedList<>();

    /**
     * ctor
     */
    public StatsMetricsGroupAttributes () {

    }


    /**
     * Add a tag to the tag list
     *
     * @param tagName
     * @param tagValue
     */
    public void addTag(String tagName, String tagValue) {

        StatsMetricsTag tag = new StatsMetricsTag(tagName,tagValue);

        metricsTags.add( tag );

    }

    public String toString() {

        StringBuilder result = new StringBuilder();

        // Group name
        result.append( String.format("GroupName=%s ", groupName) );

        // Tags
        result.append("Tags:[");
        for (StatsMetricsTag tag : metricsTags) {
            result.append( String.format(" %s=%s", tag.getName(), tag.getValue()) );
        }
        result.append(" ]");

        return result.toString();
    }

    // --- getters/setters

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<StatsMetricsTag> getMetricsTags() {
        return metricsTags;
    }

    public void setMetricsTags(List<StatsMetricsTag> metricsTags) {
        this.metricsTags = metricsTags;
    }

}
