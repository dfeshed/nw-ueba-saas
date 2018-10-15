package presidio.output.forwarder.strategy.plugins.aws;

import com.google.auto.service.AutoService;
import fortscale.utils.logging.Logger;
import presidio.output.forwarder.ForwardMassage;
import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.util.List;


/**
 * ForwarderStrategy defines a technology and logic forwarding content
 * ({@link ForwardMassage}) created by presidio from specific {@link PAYLOAD_TYPE}
 * <p>
 * This class implements forwarding strategy using Amazon Simple Queue Service
 *
 * @see <a href="https://aws.amazon.com/sqs/">amazon sqs overview</a>
 */
@AutoService(ForwarderStrategy.class)
public class SqsForwarderStrategy implements ForwarderStrategy {
    private static final Logger logger = Logger.getLogger(SqsForwarderStrategy.class);
    private static final String STRATEGY_NAME = "sqs";


    @Override
    public String getName() {
        return STRATEGY_NAME;
    }

    @Override
    public void init() {

    }

    @Override
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) {

    }

    @Override
    public void close() {
        logger.info("closing {}", this.getClass().getName());
    }
}
