package fortscale.utils.monitoring.stats;

import fortscale.utils.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(StatsMetricsGroup.class);


    // NOTE: keep the fields private because the application inherent from it

    // The stats service this metric group is registered at. Note, it might be null.
    private StatsService statsService;

    // The class being instrumented. This is typically the "service" class. It is used for logging and debugging
    private Class instrumentedClass;

    // Metric group attributes
    private StatsMetricsGroupAttributes statsMetricsGroupAttributes;

    // Holds the groupHandler of the statsService. The handler leads to the statsService
    private StatsMetricsGroupHandler statsMetricsGroupHandler;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                      of the specific service configuration class. If stats service is unavailable,
     *                                      as in most unit tests, pass a null.
     * @param instrumentedClass           - The class being instrumented. This is typically the "service" class. It is
     *                                      used for logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public StatsMetricsGroup(StatsService statsService, Class instrumentedClass,
                                          StatsMetricsGroupAttributes statsMetricsGroupAttributes) {

        // If we did not get attributes, build an empty attributes
        if (statsMetricsGroupAttributes == null) {
            statsMetricsGroupAttributes = new StatsMetricsGroupAttributes();
        }

        // If stats service is null, do nothing
        if (statsService == null) {
            logger.info("Not registering metric group {} with {} with attributes and instrumented class {} because stats service null",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());
            return;
        }

        // Save the fields
        this.instrumentedClass           = instrumentedClass;
        this.statsMetricsGroupAttributes = statsMetricsGroupAttributes;
        this.statsService                = statsService;

        logger.info("Registering metric group {} with {} attributes. Instrumented class is {}",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());

        // Register the instance to the statsService and save the handler we get in return.
        statsMetricsGroupHandler = statsService.registerStatsMetricsGroup(this);

    }

    /**
     *
     * Like manualUpdate(epochTime) except that time is the system time.
     *
     */
    public void manualUpdate() {

        // Call manualUpdate with epoch = 0 indicating system time
        manualUpdate(0);
    }

    /**
     * When the metrics fields are updated at a rate similar to (or slower than) the periodic sampling, the periodic sampling
     * shall be disabled (vid the group attributes) and manualUpdate() shall be used to trigger metrics collection.
     *
     * If the metric group does not have a stats service (typically in testing), do nothing.
     *
     * In case of a problem, an error is logger but no exception is thrown
     *
     *
     * @param epochTime - Sample time. Zero indicate, use system time.
     *
     */
    public void manualUpdate(long epochTime) {


        // If we don't have stats service, silently do nothing
        if (statsService == null) {
            logger.debug("manualUpdate() called and ignored for class {} with epochTime={} because statsService is null",
                         this.getClass().getName(), epochTime );
            return;
        }

        logger.debug("manualUpdate() called for class {} with epochTime={}", this.getClass().getName(), epochTime );

        // If we don't have group handler, just log an error
        if (statsMetricsGroupHandler == null) {
            logger.error("manualUpdate() called for class {} but handler is null. epochTime= ",
                         this.getClass().getName(), epochTime );
        }

        // We have a group handler, call it to do the real work
        statsMetricsGroupHandler.manualUpdate(epochTime);
    }

    /**
     * @return metrics group name
     */
    public String getGroupName(){

        String groupName = statsMetricsGroupHandler.getGroupName();

        return  groupName;
    }


    // --- getters/setters ---

    public Class getInstrumentedClass() {
        return instrumentedClass;
    }

    public StatsMetricsGroupAttributes getStatsMetricsGroupAttributes() {
        return statsMetricsGroupAttributes;
    }

}
