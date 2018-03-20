package presidio.output.forwarder.strategy;

public class ForwarderConfiguration {

    public ForwarderConfiguration() {
    }

    public boolean isForwardEntity(ForwarderStrategy.PAYLOAD_TYPE entityType) {
        return true;
    }

    public String getForwardingStrategy(ForwarderStrategy.PAYLOAD_TYPE entityType) {
        return "echo";
    }

    public int getForwardBulkSize(ForwarderStrategy.PAYLOAD_TYPE entityType){
        return 1;
    }

}
