package fortscale.monitoring.metrics.adapter.topicReader;

import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * class containing extra data on the Engine data message, i.e. message size
 */
public class EngineDataTopicSyncReaderResponse {
    private EngineData message;
    private long numberOfUnresolvedMessages;

    public EngineData getMessage() {
        return message;
    }

    public void setMessage(EngineData metricMessage) {
        this.message = metricMessage;
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
