package presidio.output.forwarder;

import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemoryStrategy implements ForwarderStrategy {

    public static final String MEMORY = "memory";


    List<ForwardMassage> allMessages = new ArrayList<>();
    List<ForwardMassage> lastBatchMessages = new ArrayList<>();

    @Override
    public String getName() {
        return MEMORY;
    }

    @Override
    public void init() {
        System.out.println("init memory forwarder");
    }

    @Override
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) throws Exception {
        allMessages.addAll(messages);
        lastBatchMessages = new ArrayList<>(messages);
    }

    @Override
    public void close() {
        System.out.println("closing memory forwarder");
    }


    public List<ForwardMassage> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(List<ForwardMassage> allMessages) {
        this.allMessages = allMessages;
    }

    public List<ForwardMassage> getLastBatchMessages() {
        return lastBatchMessages;
    }

    public void setLastBatchMessages(List<ForwardMassage> lastBatchMessages) {
        this.lastBatchMessages = lastBatchMessages;
    }

    public void cleanAll() {
        allMessages = new ArrayList<ForwardMassage>();
        lastBatchMessages = new ArrayList<ForwardMassage>();
    }
}
