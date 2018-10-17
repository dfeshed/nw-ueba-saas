package presidio.output.forwarder.strategy;

public class ForwarderConfiguration {

    private final String forwarderStrategy;

    public ForwarderConfiguration(String forwarderStrategy) {
        this.forwarderStrategy = forwarderStrategy;
    }

    public boolean isForwardEntity(ForwarderStrategy.PAYLOAD_TYPE entityType) {
        return ForwarderStrategy.PAYLOAD_TYPE.INDICATOR.equals(entityType);
    }

    public String getForwardingStrategy(ForwarderStrategy.PAYLOAD_TYPE entityType) {
        return forwarderStrategy;
    }

    public int getForwardBulkSize(ForwarderStrategy.PAYLOAD_TYPE entityType){
        return 1;
    }

    public boolean extendEntity(ForwarderStrategy.PAYLOAD_TYPE entityType) {return true;}

}
