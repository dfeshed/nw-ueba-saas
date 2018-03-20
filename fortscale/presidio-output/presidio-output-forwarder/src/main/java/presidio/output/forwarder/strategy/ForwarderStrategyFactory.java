package presidio.output.forwarder.strategy;

import fortscale.utils.logging.Logger;
import presidio.output.forwarder.shell.OutputForwarderApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ForwarderStrategyFactory {

    private static final Logger logger = Logger.getLogger(ForwarderStrategyFactory.class);

    Map<String, ForwarderStrategy> forwarderStrategiesMap;

    public ForwarderStrategyFactory() {

        ServiceLoader<ForwarderStrategy> serviceLoader = ServiceLoader.load(ForwarderStrategy.class);

        forwarderStrategiesMap = new HashMap<>();
        for (ForwarderStrategy strategy : serviceLoader) {

            if (forwarderStrategiesMap.containsKey(strategy.getName())) {
                logger.warn("{} strategy already loaded. skipping {}", strategy.getName(), strategy.getClass().getName());
                continue;
            }

            forwarderStrategiesMap.put(strategy.getName(),strategy);

            logger.debug("{} strategy loaded successfully, implementation: {}", strategy.getName(), strategy.getClass().getName());
        }
    }

    public ForwarderStrategy getStrategy(String type) {
        return forwarderStrategiesMap.get(type);
    }

    @PostConstruct
    public void init() throws Exception {
        forwarderStrategiesMap.values().forEach(strategy -> strategy.init());
    }

    @PreDestroy
    public void close() throws Exception {
        forwarderStrategiesMap.values().forEach(strategy -> strategy.close());
    }

}
