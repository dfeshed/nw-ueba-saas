package presidio.output.forwarder;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import fortscale.utils.logging.Logger;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
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

    protected ForwardedEntity doForward(Stream<T> instances) {

        ForwarderStrategy.PAYLOAD_TYPE payloadType = getPayloadType();

        if (!forwarderConfiguration.isForwardEntity(payloadType)) {
            return new ForwardedEntity(0, new ArrayList<>());
        }

        // select forwarding strategy
        String strategy = forwarderConfiguration.getForwardingStrategy(payloadType);
        ForwarderStrategy forwarderStrategy = forwarderStrategyFactory.getStrategy(strategy);
        if (forwarderStrategy == null) {
            String errorMsg = String.format("Forwarding strategy %s doesn't exist in the system", forwarderStrategy);
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        int bulkSize = forwarderConfiguration.getForwardBulkSize(payloadType);

        // doForward messages in a batch
        final AtomicInteger forwardedCount = new AtomicInteger();
        List<String> ids = new ArrayList<>();
        Iterators.partition(instances.iterator(), bulkSize).forEachRemaining(instancesBulk -> {
            try {
                instancesBulk.forEach(instance -> ids.add(getId(instance)));
                int success = forwardBatch(forwarderStrategy, payloadType, instancesBulk);
                forwardedCount.addAndGet(success);
            } catch (Exception ex) {
                logger.error("failed to doForward bulk '{}'", instancesBulk);
                Throwables.propagate(ex);
            }
        });
        logger.info("{} '{}' messages were forwarded successfully", forwardedCount.get(),  getPayloadType());
        return new ForwardedEntity(forwardedCount.get(), ids);
    }


    private int forwardBatch(ForwarderStrategy forwarderStrategy, ForwarderStrategy.PAYLOAD_TYPE payloadType, List<T> entities) throws Exception{
        List<ForwardMassage> messages = new ArrayList<>();
        entities.forEach(entity -> {
                    try {
                        ForwardMassage message = new ForwardMassage(getId(entity),buildPayload(entity), buildHeader(entity) );
                        messages.add(message);
                    } catch (Exception ex) {
                        logger.error("failed to build payload '{}': {}", payloadType, entity);
                        Throwables.propagate(ex);
                    }
                });
        forwarderStrategy.forward(messages, payloadType);
        return messages.size();
    };

    abstract String getId(T entity);

    abstract String buildPayload(T entity) throws Exception;

    abstract Map buildHeader(T entity) throws Exception;

    abstract ForwarderStrategy.PAYLOAD_TYPE getPayloadType();

    public class ForwardedEntity {
        private int forwardedCount;
        private List<String> ids;

        public ForwardedEntity(int forwardedCount, List<String> ids){
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
