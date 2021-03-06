package presidio.output.forwarder;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import fortscale.utils.logging.Logger;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class Forwarder<T>{

    private static final Logger logger = Logger.getLogger(Forwarder.class);

    ForwarderConfiguration forwarderConfiguration;
    ForwarderStrategyFactory forwarderStrategyFactory;


    public Forwarder(ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        this.forwarderConfiguration = forwarderStrategyConfiguration;
        this.forwarderStrategyFactory = forwarderStrategyFactory;
    }

    protected ForwardedInstances doForward(Stream<T> instances, boolean needIds) {

        ForwarderStrategy.PAYLOAD_TYPE payloadType = getPayloadType();

        boolean skipForward = !forwarderConfiguration.isForwardInstance(payloadType);

        ForwarderStrategy forwarderStrategy = !skipForward ? getStrategy(payloadType) : null;

        int bulkSize = forwarderConfiguration.getForwardBulkSize(payloadType);

        // forward messages in a batch
        final AtomicInteger forwardedCount = new AtomicInteger();
        List<String> ids = new ArrayList<>();
        Iterators.partition(instances.iterator(), bulkSize).forEachRemaining(instancesBulk -> {
            try {
                if(needIds){
                    instancesBulk.forEach(instance -> ids.add(getId(instance)));
                }
                if(!skipForward){
                    int success = forwardBatch(forwarderStrategy, payloadType, instancesBulk);
                    forwardedCount.addAndGet(success);
                }

            } catch (Exception ex) {
                logger.error("failed to forward bulk '{}'", instancesBulk);
                Throwables.propagate(ex);
            }
        });
        logger.info("{} '{}' messages were forwarded successfully", forwardedCount.get(),  getPayloadType());
        return new ForwardedInstances(forwardedCount.get(), ids);
    }

    private ForwarderStrategy getStrategy(ForwarderStrategy.PAYLOAD_TYPE payloadType){
        // select forwarding strategy
        String strategy = forwarderConfiguration.getForwardingStrategy(payloadType);
        ForwarderStrategy forwarderStrategy = forwarderStrategyFactory.getStrategy(strategy);
        if (forwarderStrategy == null) {
            String errorMsg = String.format("Forwarding strategy %s doesn't exist in the system", strategy);
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return forwarderStrategy;
    }


    private int forwardBatch(ForwarderStrategy forwarderStrategy, ForwarderStrategy.PAYLOAD_TYPE payloadType, List<T> instances) throws Exception{
        List<ForwardMassage> messages = new ArrayList<>();
        instances.forEach(instance -> {
                    try {
                        ForwardMassage message = new ForwardMassage(getId(instance),buildPayload(instance), buildHeader(instance) );
                        messages.add(message);
                    } catch (Exception ex) {
                        logger.error("failed to build payload '{}': {}", payloadType, instance);
                        Throwables.propagate(ex);
                    }
                });
        forwarderStrategy.forward(messages, payloadType);
        return messages.size();
    };

    abstract String getId(T instance);

    abstract String buildPayload(T instance) throws Exception;

    abstract Map buildHeader(T instance) throws Exception;

    abstract ForwarderStrategy.PAYLOAD_TYPE getPayloadType();

    public class ForwardedInstances {
        private int forwardedCount;
        private List<String> ids;

        public ForwardedInstances(int forwardedCount, List<String> ids){
            this.forwardedCount = forwardedCount;
            this.ids = ids;
        }

        public int getForwardedCount() {
            return forwardedCount;
        }
        public List<String> getIds() {
            return ids;
        }
    }
}
