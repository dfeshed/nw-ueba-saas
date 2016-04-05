package fortscale.services.monitoring.stats;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaashh on 4/5/16.
 */

// TODO: Add tag validations
// TODO: Add measurement name validation
// TODO: Add name
public class StatsMetricsGroupAttributes {

    protected String                groupName;
    protected List<StatsMetricsTag> metricsTags;
    protected StatsService          statsService; // Might be null

    public StatsMetricsGroupAttributes () {

        metricsTags = new LinkedList<>();
    }


    public void addTag(String tagName, String tagValue) {

        StatsMetricsTag tag = new StatsMetricsTag(tagName,tagValue);

        metricsTags.add( tag );

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

    public StatsService getStatsService() {
        return statsService;
    }

    public void setStatsService(StatsService statsService) {
        this.statsService = statsService;
    }

}
