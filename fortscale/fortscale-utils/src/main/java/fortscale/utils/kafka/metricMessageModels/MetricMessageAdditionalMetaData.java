package fortscale.utils.kafka.metricMessageModels;

/**
 * class containing extra data on the meteric message, i.e. message size
 */
public class MetricMessageAdditionalMetaData {
    public MetricMessage getMetricMessage() {
        return metricMessage;
    }

    public void setMetricMessage(MetricMessage metricMessage) {
        this.metricMessage = metricMessage;
    }

    public long getMetricMessageSize() {
        return metricMessageSize;
    }

    public void setMetricMessageSize(long metricMessageSize) {
        this.metricMessageSize = metricMessageSize;
    }

    private MetricMessage metricMessage;
    private long metricMessageSize;
}
