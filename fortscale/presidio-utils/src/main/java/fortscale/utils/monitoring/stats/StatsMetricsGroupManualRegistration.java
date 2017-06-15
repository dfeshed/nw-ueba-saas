package fortscale.utils.monitoring.stats;

/**
 *
 * A special variant of StatsMetricsGroup class to be used with manual (instead of) automatic registration to the
 * stats service. Manual registration should be used only in special cases when the metrics group class has to be
 * created before the stats service is available, like with statsServiceSelfMetrics class.
 *
 * Created by gaashh on 11/21/16.
 */
public class StatsMetricsGroupManualRegistration extends StatsMetricsGroup {

    /**
     * A special ctor for manual registration metrics group. Manual registration should be used only in special cases
     * when the metrics group class has to be created before the stats service is available, like with
     * statsServiceSelfMetrics class.
     * The ctor initialize the class but it does not register the metrics group to the stats service. This should be
     * done with manualRegister().
     *
     * @param instrumentedClass           - The class being instrumented. This is typically the "service" class. It is
     *                                      used for logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public StatsMetricsGroupManualRegistration(Class instrumentedClass, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(instrumentedClass, statsMetricsGroupAttributes);

    }

    /**
     * Registers a metrics group to stats service. It should be used only with the classes created with the special ctor
     * for manual registration.
     * Manual registration should be used only in special cases when the metrics group class has to be created before
     * the stats service is available, like with statsServiceSelfMetrics class.
     *
     * @param statsService - stats service to register to
     */
    public void manualRegister(StatsService statsService) {
        super.manualRegister(statsService);
    }
}
