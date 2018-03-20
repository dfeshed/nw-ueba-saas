package presidio.output.forwarder;

import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemoryStrategy implements ForwarderStrategy {

    public static final String MEMORY = "memory";


    List<String> allMessages = new ArrayList<String>();
    List<String> lastBatchMessages = new ArrayList<String>();

    @Override
    public String getName() {
        return MEMORY;
    }

    @Override
    public void init() {
        System.out.println("init memory forwarder");
    }

    @Override
    public void forward(Map<String, String> messages, PAYLOAD_TYPE type) throws Exception {
        allMessages.addAll(messages.values());
        lastBatchMessages = new ArrayList<>(messages.values());
    }

    @Override
    public void close() {
        System.out.println("closing memory forwarder");
    }


    public List<String> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(List<String> allMessages) {
        this.allMessages = allMessages;
    }

    public List<String> getLastBatchMessages() {
        return lastBatchMessages;
    }

    public void setLastBatchMessages(List<String> lastBatchMessages) {
        this.lastBatchMessages = lastBatchMessages;
    }

    public void cleanAll() {
        allMessages = new ArrayList<String>();
        lastBatchMessages = new ArrayList<String>();
    }
}
