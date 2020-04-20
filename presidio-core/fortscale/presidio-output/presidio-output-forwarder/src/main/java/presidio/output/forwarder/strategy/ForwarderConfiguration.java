package presidio.output.forwarder.strategy;

import java.util.Collections;
import java.util.Set;

public class ForwarderConfiguration {

    private final String forwarderStrategy;
    private final Set<ForwarderStrategy.PAYLOAD_TYPE> payloadTypesToForward;

    public ForwarderConfiguration(String forwarderStrategy) {
        this.forwarderStrategy = forwarderStrategy;
        this.payloadTypesToForward = Collections.singleton(ForwarderStrategy.PAYLOAD_TYPE.INDICATOR);
    }

    public boolean isForwardInstance(ForwarderStrategy.PAYLOAD_TYPE instanceType) {
        return payloadTypesToForward.contains(instanceType);
    }

    public String getForwardingStrategy(ForwarderStrategy.PAYLOAD_TYPE instanceType) {
        return forwarderStrategy;
    }

    public int getForwardBulkSize(ForwarderStrategy.PAYLOAD_TYPE instanceType){
        return 1;
    }

    public boolean extendInstance(ForwarderStrategy.PAYLOAD_TYPE instanceType) {return true;}

}
