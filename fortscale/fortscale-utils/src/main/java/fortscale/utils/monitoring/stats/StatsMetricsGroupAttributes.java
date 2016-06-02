package fortscale.utils.monitoring.stats;

import fortscale.utils.logging.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * This class holds a metrics group attributes. It is a parameter to the StatsMetricsGroup ctor.
 *
 * It holds:
 * 1. Group name       - the metrics group name (measurement name). Note that group name annotation has priority over this
 * 2. Tag list         - a list of tags names and tag values to be attached to the group
 * 3. ManualUpdateMode - If true, the metrics group will be updated using the manualUpdate() rather than automatic
 *                       periodic update. Default is automatic updates
 * 4. override process name and process group - if set, those values override the values set by the stats service
 *
 * Created by gaashh on 4/5/16.
 */

// TODO: Add tag validations. Also make sure automatic tags are not added
// TODO: Add measurement name validation
public class StatsMetricsGroupAttributes {

    private static final Logger logger = Logger.getLogger(StatsMetricsGroupAttributes.class);

    // The metrics group name (measurement name). Note that group name annotation has priority over this
    protected String                groupName;

    // Tag list - a list of tags names and tag values to be attached to the group
    protected List<StatsMetricsTag> metricsTags = new LinkedList<>();

    // True -> metrics group will be updated via manualUpdate(). False -> automatic updates using periodic updates thread
    boolean isManualUpdateMode = false;

    // If set, overrides the process name set by the stats service for automatic tags
    String overrideProcessName;

    // If set, overrides the process group name set by the stats service for automatic tags
    String overrideProcessGroupName;

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

        // Check null tag name
        if (tagName == null) {
            tagName = "(NULL)";
            logger.warn("null tag name was added");
        }

        // Check null value  name
        if (tagValue == null) {
            tagValue = "(NULL)";
            logger.warn("null tag value was added");
        }

        StatsMetricsTag tag = new StatsMetricsTag(tagName,tagValue);

        metricsTags.add( tag );

    }

    /**
     * Overrides process name and process group name that are added to the tags automatically.
     *
     * By default those values are set by the stats service
     *
     * This function should be used only in special cases
     *
     * @param overrideProcessName        - process name to override with
     * @param overrideProcessGroupName   - process group name to override with
     */
    public void overrideProcessName(String overrideProcessName, String overrideProcessGroupName) {

        logger.debug("Overriding process name to {} and process group name to {}",
                      overrideProcessName, overrideProcessGroupName);

        // Verify process name is not null
        if (overrideProcessName == null) {
            throw new NullPointerException("overrideProcessName is null");
        }

        // Verify process group name is not null
        if (overrideProcessGroupName == null) {
            throw new NullPointerException("overrideProcessGroupName is null");
        }

        // All OK, save the values
        this.overrideProcessName      = overrideProcessName;
        this.overrideProcessGroupName = overrideProcessGroupName;
    }


    public String toString() {

        StringBuilder result = new StringBuilder();

        // Group name
        result.append( String.format("GroupName=%s isManualUpdateMode=%b ", groupName, isManualUpdateMode) );

        // Tags
        result.append( String.format("Tags:[%s]", StatsMetricsTag.metricsTagListToString(metricsTags)) );

        // Process name overrides
        result.append( String.format(" overrideProcessName=%s overrideProcessGroupName=%s",
                                     overrideProcessName, overrideProcessGroupName) );

        return result.toString();
    }


    public String toStringShort() {

        String result = String.format("Tags:[%s]", StatsMetricsTag.metricsTagListToString(metricsTags));

        return result;
    }


    // --- getters/setters

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOverrideProcessName() {
        return overrideProcessName;
    }

    public String getOverrideProcessGroupName() {
        return overrideProcessGroupName;
    }

    public List<StatsMetricsTag> getMetricsTags() {
        return metricsTags;
    }

    public void setMetricsTags(List<StatsMetricsTag> metricsTags) {
        this.metricsTags = metricsTags;
    }

    public boolean isManualUpdateMode() {
        return isManualUpdateMode;
    }

    public void setManualUpdateMode(boolean isManualUpdateMode) {
        this.isManualUpdateMode = isManualUpdateMode;
    }
}
