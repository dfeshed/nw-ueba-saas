package fortscale.monitoring.metricAdapter.stats;

import org.joda.time.DateTime;

import java.lang.reflect.Field;

/**
 * metric adapter stats monitoring counters
 */
public class MetricAdapterStats {
    private long epochTime = 0;
    private long numberOfWrittenPoints = 0;
    private long numberOfWrittenPointsBytes = 0;
    private long numberOfReadMetricMessages = 0;
    private long numberOfReadMetricMessagesBytes = 0;
    private long numberOfReadEngineDataMessages = 0;
    private long numberOfReadEngineDataMessagesBytes = 0;


    public void add(String fieldName, long value) {
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
