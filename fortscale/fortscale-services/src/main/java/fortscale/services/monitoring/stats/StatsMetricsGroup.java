package fortscale.services.monitoring.stats;

/**
 * StatsMetrics group is heavily used by the application. To add metrics group the application shall extend this class,
 * provide the metrics fields.
 *
 * The class ctor automatically registers the class at the stats services.
 *
 * The StatsMetricsGroupParams annotation can add additional info to the metrics group.
 *
 * The application class contains fields. A field is designated as metric fields by the Stats<Type>MetricParams
 * annotation. Note that multiple annotations can be applied to the same field generating multiple matrices from one
 * field
 *
 * See Stats<Type>MetricParams for supported field data types
 *
 * Typically the fields are sample via period thread. Care should be taken with multi-threaded access and locking.
 *
 * When the metrics fields are updated at a rate similar to (or slower than) the periodic sampling, the periodic sampling
 * shall be disabled (vid the group attributes) and manualUpdate() shall be used to trigger metrics collection.
 *
 *
 * Created by gaashh on 4/3/16.
 */
public class StatsMetricsGroup {

    // NOTE: keep the fields private because the application inherent from it

    // The class being instrumented. This is typically the "service" class. It is used for logging and debugging
    private Class instrumentedClass;

    // Metric group attributes
    private StatsMetricsGroupAttributes statsMetricsGroupAttributes;

    // Holds the groupHandler of the statsService. The handler leads to the statsService
    private StatsMetricsGroupHandler statsMetricsGroupHandler;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param instrumentedClass The class being instrumented. This is typically the "service" class. It is used for
     *                          logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
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

    /**
     * When the metrics fields are updated at a rate similar to (or slower than) the periodic sampling, the periodic sampling
     * shall be disabled (vid the group attributes) and manualUpdate() shall be used to trigger metrics collection.
     *
     * The sample time is taken is set to the system time.
     *
     */
    public void manualUpdate() {
        statsMetricsGroupHandler.manualUpdate();
    }

    /**
     * When the metrics fields are updated at a rate similar to (or slower than) the periodic sampling, the periodic sampling
     * shall be disabled (vid the group attributes) and manualUpdate() shall be used to trigger metrics collection.
     *
     * The sample time given.
     *
     */
    public void manualUpdate(long epochTime) {
        statsMetricsGroupHandler.manualUpdate(epochTime);
    }

    // --- getters/setters ---

    public Class getInstrumentedClass() {
        return instrumentedClass;
    }

    public StatsMetricsGroupAttributes getStatsMetricsGroupAttributes() {
        return statsMetricsGroupAttributes;
    }

}
