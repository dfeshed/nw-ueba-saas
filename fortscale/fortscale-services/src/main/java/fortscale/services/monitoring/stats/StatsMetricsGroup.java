package fortscale.services.monitoring.stats;

/**
 * Created by gaashh on 4/3/16.
 */
public class StatsMetricsGroup {

    // NOTE: keep the fields private because the application inherent from it

    private Class instrumentedClass;
    private StatsMetricsGroupAttributes statsMetricsGroupAttributes;

    // Holds the groupHandler of the statsSevice. It is also used to find the statsService
    private StatsMetricsGroupHandler statsMetricsGroupHandler;

    // ctor - register the class at statsService and create the groupHandler

    public StatsMetricsGroup(Class instrumentedClass, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {

        // If we did not get attributes, create an empty attributes
        if (statsMetricsGroupAttributes == null) {
            statsMetricsGroupAttributes = new StatsMetricsGroupAttributes();
        }

        // Save the fields
        this.instrumentedClass           = instrumentedClass;
        this.statsMetricsGroupAttributes = statsMetricsGroupAttributes;

        // Get statsService from attributes. In most case the service would be null. In this case, resolve it via Spring
        StatsService statsService = this.statsMetricsGroupAttributes.statsService;
        if ( statsService == null) {
            statsService = null; // TODO: resolve via spring, make it singletone
        }

        // Register the instance to the statsService and save the handler we get in return.
        statsMetricsGroupHandler = statsService.registerStatsMetricsGroup(this);

    }

    // --- getters/setters ---

    public Class getInstrumentedClass() {
        return instrumentedClass;
    }

    public StatsMetricsGroupAttributes getStatsMetricsGroupAttributes() {
        return statsMetricsGroupAttributes;
    }

}
