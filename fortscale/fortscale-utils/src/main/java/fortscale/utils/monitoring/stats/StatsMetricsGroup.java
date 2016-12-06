package fortscale.utils.monitoring.stats;

import fortscale.utils.logging.Logger;

import java.util.concurrent.atomic.AtomicLong;

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

    // Two (static) counters holding the metrics group registration and manual update with null stats service
    // The counters are collected by StatsServiceSelfMetrics
    private static AtomicLong registerWithNullStatsServiceCounter = new AtomicLong();
    private static AtomicLong manualUpdateWithNullStatsServiceCounter = new AtomicLong();


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

        // Save the fields (some of them are accessed even if statsService is null at unregister() )
        this.instrumentedClass           = instrumentedClass;
        this.statsMetricsGroupAttributes = statsMetricsGroupAttributes;
        this.statsService                = statsService;


        // If stats service is null, do nothing
        if (statsService == null) {

            registerWithNullStatsServiceCounter.incrementAndGet();

            logger.info("Not registering metric group {} with {} with attributes and instrumented class {} because stats service null",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());
            return;
        }

        // Log it if enabled
        if (logger.isDebugEnabled()) {
            logger.debug("Registering metric group {} with {} attributes. Instrumented class is {}",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());
        }

        // Register the instance to the statsService and save the handler we get in return.
        statsMetricsGroupHandler = statsService.registerStatsMetricsGroup(this);

    }

    /**
     * A special ctor for manual registration metrics group. Manual registration should be used only in special cases
     * when the metrics group class has to be created before the stats service is available, like with
     * statsServiceSelfMetrics class.
     * The ctor initialize the class but it does not register the metrics group to the stats service. This should be
     * done with manualRegister().
     * The ctor is "protected" to ensure it will be used only via StatsMetricsGroupManualRegistration class.
     *
     * @param instrumentedClass           - The class being instrumented. This is typically the "service" class. It is
     *                                      used for logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    protected StatsMetricsGroup(Class instrumentedClass, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {

        // If we did not get attributes, build an empty attributes
        if (statsMetricsGroupAttributes == null) {
            statsMetricsGroupAttributes = new StatsMetricsGroupAttributes();
        }

        // Save the fields (some of them are accessed even if statsService is null at unregister() )
        this.instrumentedClass = instrumentedClass;
        this.statsMetricsGroupAttributes = statsMetricsGroupAttributes;

    }

    /**
     * Registers a metrics group to stats service. It should be used only with the classes created with the special ctor
     * for manual registration.
     * Manual registration should be used only in special cases when the metrics group class has to be created before
     * the stats service is available, like with statsServiceSelfMetrics class.
     *
     * The method is "protected" to ensure it will be used only via StatsMetricsGroupManualRegistration class.
     *
     *
     * @param statsService - stats service to register to
     */
    protected void manualRegister(StatsService statsService) {

        // Verify not already registered
        if (this.statsService != null) {
            String msg = String.format("Class %s is already registered", this.getClass());
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        // Save the stats service
        this.statsService = statsService;

        // If stats service is null, do nothing
        if (statsService == null) {

            registerWithNullStatsServiceCounter.incrementAndGet();

            logger.info("Not manually registering metric group {} with {} with attributes and instrumented class {} because stats service null",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());

            return;
        }

        // Log it if enabled
        if (logger.isDebugEnabled()) {
            logger.debug("Manually registering metric group {} with {} attributes. Instrumented class is {}",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());
        }

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
            manualUpdateWithNullStatsServiceCounter.incrementAndGet();

            logger.debug("manualUpdate() called and ignored for class {} with epochTime={} because statsService is null",
                         this.getClass().getName(), epochTime );
            return;
        }

        logger.debug("manualUpdate() called for class {} with epochTime={}", this.getClass().getName(), epochTime );

        // If we don't have group handler, just log an error
        if (statsMetricsGroupHandler == null) {
            logger.error("manualUpdate() called for class {} but handler is null. epochTime={}",
                         this.getClass().getName(), epochTime );
            return;
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


    /**
     *
     * Unregister the metrics group from the stats service (if any)
     *
     */
    public void unregister() {

        // If stats service is null, do nothing
        if (statsService == null) {
            logger.info("Ignoring unregister metric group {} with {} with attributes and instrumented class {} because stats service null",
                    this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());
            return;
        }

        // Unregister from stats service
        logger.debug("Unregistering metric group {} with {} attributes. Instrumented class is {}",
                this.getClass().getName(), statsMetricsGroupAttributes.toString(), instrumentedClass.getName());

        statsService.unregisterStatsMetricsGroup(this);

    }

    // --- getters/setters ---

    public Class getInstrumentedClass() {
        return instrumentedClass;
    }

    public StatsMetricsGroupAttributes getStatsMetricsGroupAttributes() {
        return statsMetricsGroupAttributes;
    }

    public StatsMetricsGroupHandler getStatsMetricsGroupHandler() {
        return statsMetricsGroupHandler;
    }

    public static AtomicLong getRegisterWithNullStatsServiceCounter() {
        return registerWithNullStatsServiceCounter;
    }

    public static AtomicLong getManualUpdateWithNullStatsServiceCounter() {
        return manualUpdateWithNullStatsServiceCounter;
    }
}
