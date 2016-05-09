package fortscale.monitoring.metricAdapter.stats;

import fortscale.monitoring.metricAdapter.MetricAdapter;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

/**
 * metric adapter stats monitoring counters
 */
public class MetricAdapterMetric extends StatsMetricsGroup {
    @StatsLongMetricParams
    private long epochTime = 0;
    @StatsLongMetricParams
    private long numberOfWrittenPoints = 0;
    @StatsLongMetricParams
    private long numberOfWrittenPointsBytes = 0;
    @StatsLongMetricParams
    private long numberOfReadMetricMessages = 0;
    @StatsLongMetricParams
    private long numberOfReadMetricMessagesBytes = 0;
    @StatsLongMetricParams
    private long numberOfReadEngineDataMessages = 0;
    @StatsLongMetricParams
    private long numberOfReadEngineDataMessagesBytes = 0;
    @StatsLongMetricParams
    private long numberOfUnresolvedMetricMessages=0;

    /**
     * The ctor, in addition to initializing the class, registers the metrics group to the stats service.
     *
     * @param statsService                - The stats service to register to. Typically it is obtained via @Autowired
     *                                    of the specific service configuration class. If stats service is unavailable,
     *                                    as in most unit tests, pass a null.
     * @param instrumentedClass           - The class being instrumented. This is typically the "service" class. It is
     *                                    used for logging and debugging
     * @param statsMetricsGroupAttributes - metrics group attributes (e.g. tag list). Might be null.
     */
    public MetricAdapterMetric(StatsService statsService, Class instrumentedClass, StatsMetricsGroupAttributes statsMetricsGroupAttributes) {
        super(statsService, instrumentedClass, statsMetricsGroupAttributes);
    }

    /**
     * adds value to long field. i.e. metricAdapterMetric.addLong("numberOfWrittenPoints", 3); will addLong 3 to the field numberOfWrittenPoints
     * @param fieldName the field we want to update
     * @param value the value we want to add to the field
     */
    public void addLong(String fieldName, long value) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setLong(this, field.getLong(this) + value);
            this.epochTime = DateTime.now().getMillis();
        }
        catch (Exception e)
        {
            throw new RuntimeException(String.format("field %s is not accessible",fieldName),e);
        }
    }


}
