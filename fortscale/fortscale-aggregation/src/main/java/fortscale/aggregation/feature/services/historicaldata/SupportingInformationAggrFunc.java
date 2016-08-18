package fortscale.aggregation.feature.services.historicaldata;

/**
 * Enum for supporting information aggregation function types
 *
 * @author gils
 * Date: 12/08/2015
 */
public enum SupportingInformationAggrFunc {
    Count, // used mostly for histograms
    HourlyCountGroupByDayOfWeek, // e.g. heatmap
    DistinctEventsByTime, // e.g. aggregated # of events by time
    TimeIntervals, // e.g. VPN session overlapping
    VPNSession,
    VPNLateralMovement
}
