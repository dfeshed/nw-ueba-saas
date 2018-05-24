package presidio.webapp.model;

import java.time.Instant;

public class Metric {

    private final String metricName;
    private final Number metricValue;
    private final String dataType;
    private final Instant reportTime;
    private final Instant logicalTime;

    public Metric(String metricName, Number metricValue, String dataType, Instant reportTime, Instant logicalTime) {
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.dataType = dataType;
        this.reportTime = reportTime;
        this.logicalTime = logicalTime;
    }

    public String getMetricName() {
        return metricName;
    }

    public Number getMetricValue() {
        return metricValue;
    }

    public String getDataType() {
        return dataType;
    }

    public Instant getReportTime() {
        return reportTime;
    }

    public Instant getLogicalTime() {
        return logicalTime;
    }
}
