package fortscale.monitoring.samza.topicReader;

import fortscale.utils.samza.metricMessageModels.MetricMessage;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * class containing extra data on the meteric message, i.e. message size
 */
public class SamzaMetricsTopicSyncReaderResponse {
    private MetricMessage metricMessage;
    private long numberOfUnresolvedMessages;

    public MetricMessage getMetricMessage() {
        return metricMessage;
    }

    public void setMetricMessage(MetricMessage metricMessage) {
        this.metricMessage = metricMessage;
    }

    public long getNumberOfUnresolvedMessages() {
        return numberOfUnresolvedMessages;
    }

    public void addUnresolvedMessages() {
        this.numberOfUnresolvedMessages++;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
