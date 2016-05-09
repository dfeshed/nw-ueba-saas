package fortscale.monitoring.samza.topicReader;

import fortscale.utils.samza.metricMessageModels.MetricMessage;

/**
 * class containing extra data on the meteric message, i.e. message size
 */
public class SamzaMetricsTopicSyncReaderResponse {
    private MetricMessage metricMessage;
    private long metricMessageSize;
    private long numberOfUnresolvedMessages;

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

    public long getNumberOfUnresolvedMessages() {
        return numberOfUnresolvedMessages;
    }

    public void addUnresolvedMessages() {
        this.numberOfUnresolvedMessages++;
    }
}
