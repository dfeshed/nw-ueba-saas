package fortscale.monitoring.external.stats.samza.collector.topicReader;

import fortscale.utils.samza.metricMessageModels.MetricMessage;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * class containing extra data on the metric message, i.e. message size
 */
public class SamzaMetricsTopicSyncReaderResponse {

    private List<MetricMessage> metricMessages;
    private long numberOfUnresolvedMessages;

    /**
     * ctor
     */
    public SamzaMetricsTopicSyncReaderResponse() {
        metricMessages = new ArrayList<>();
    }

    public List<MetricMessage> getMetricMessages() {
        return metricMessages;
    }

    public void addMetricMessages(MetricMessage metricMessages) {
        this.metricMessages.add(metricMessages);
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
