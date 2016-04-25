package fortscale.monitoring.metricAdapter.stats;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * metric adapter stats monitoring counters
 */
@Configurable(preConstruction = true)
public class MetricAdapterStats {
    private Long epochTime = 0L;
    private Long eventsWrittenToInflux = 0L;
    private Long eventsReadFromMetricsTopic = 0L;
    private Long engineDataEventsReadFromMetricsTopic = 0L;

    public MetricAdapterStats(){

    }
    public Long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = epochTime;
    }

    public Long getEventsWrittenToInflux() {
        return eventsWrittenToInflux;
    }

    public void setEventsWrittenToInflux(Long eventsWrittenToInflux) {
        this.epochTime = DateTime.now().getMillis();
        this.eventsWrittenToInflux = eventsWrittenToInflux;
    }

    public Long getEventsReadFromMetricsTopic() {
        return eventsReadFromMetricsTopic;
    }

    public void setEventsReadFromMetricsTopic(Long eventsReadFromMetricsTopic) {
        this.epochTime = DateTime.now().getMillis();
        this.eventsReadFromMetricsTopic = eventsReadFromMetricsTopic;
    }

    public Long getEngineDataEventsReadFromMetricsTopic() {
        return engineDataEventsReadFromMetricsTopic;
    }

    public void setEngineDataEventsReadFromMetricsTopic(Long engineDataEventsReadFromMetricsTopic) {
        this.epochTime = DateTime.now().getMillis();
        this.engineDataEventsReadFromMetricsTopic = engineDataEventsReadFromMetricsTopic;
    }


}
