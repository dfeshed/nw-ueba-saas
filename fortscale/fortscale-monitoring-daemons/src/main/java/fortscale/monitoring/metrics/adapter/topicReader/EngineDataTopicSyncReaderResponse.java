package fortscale.monitoring.metrics.adapter.topicReader;

import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * class containing extra data on the Engine data messages, i.e. messages size
 */
public class EngineDataTopicSyncReaderResponse {
    private List<EngineData> messages;
    private long numberOfUnresolvedMessages;

    public List<EngineData> getMessages() {
        return messages;
    }

    public EngineDataTopicSyncReaderResponse() {
        numberOfUnresolvedMessages=0;
        messages = new ArrayList<>();
    }

    public void addMessage(EngineData metricMessage) {
        this.messages.add(metricMessage);
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
