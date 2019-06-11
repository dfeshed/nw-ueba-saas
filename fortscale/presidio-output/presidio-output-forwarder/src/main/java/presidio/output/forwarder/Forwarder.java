package presidio.output.forwarder;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import fortscale.utils.logging.Logger;
import presidio.output.domain.records.alerts.Alert;
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
    protected List<String> alertIds = new ArrayList<>();

    ForwarderConfiguration forwarderConfiguration;
    ForwarderStrategyFactory forwarderStrategyFactory;


    public Forwarder(ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
        this.forwarderConfiguration = forwarderStrategyConfiguration;
        this.forwarderStrategyFactory = forwarderStrategyFactory;
    }

    public int forward(Instant startDate, Instant endDate, String entityType, boolean isAlerts) {

        ForwarderStrategy.PAYLOAD_TYPE payloadType = getPayloadType();

        if (!forwarderConfiguration.isForwardEntity(payloadType)) {
            return 0;
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


        // forward messages in a batch
        final AtomicInteger forwardedCount = new AtomicInteger();
        try (Stream<T> entities = getEntitiesToForward(startDate, endDate, entityType)){
            Iterators.partition(entities.iterator(), bulkSize).forEachRemaining(entitiesBulk -> {
                try {
                    if(isAlerts){
                        entitiesBulk.forEach(alert -> alertIds.add(getId(alert)));
                    }
                    int success = forwardBatch(forwarderStrategy, payloadType, entitiesBulk);
                    forwardedCount.addAndGet(success);
                } catch (Exception ex) {
                    logger.error("failed to forward bulk '{}'", entitiesBulk);
                    Throwables.propagate(ex);
                }
            });
        }
        logger.info("{} '{}' messages were forwarded successfully", forwardedCount.get(),  getPayloadType());
        return forwardedCount.get();
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

    abstract Stream<T> getEntitiesToForward(Instant startDate, Instant endDate, String entityType);

    abstract String getId(T entity);

    abstract String buildPayload(T entity) throws Exception;

    abstract Map buildHeader(T entity) throws Exception;

    abstract ForwarderStrategy.PAYLOAD_TYPE getPayloadType();

}
