package fortscale.streaming.task.enrichment;

import com.google.common.collect.Iterables;
import fortscale.domain.core.Computer;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.services.CachingService;
import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import fortscale.streaming.service.ipresolving.EventsIpResolvingService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.StringPredicates;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Streaming task that receive events from input streams and resolves ip addresses in each event according to a
 * configuration saying which fields are to be resolved and with which resolving configuration (e.g. only AD names, etc)
 */
public class IpResolvingStreamTask extends AbstractStreamTask {

    // map between input topic name and relevant resolving cache instance
    private static Map<String, CachingService> topicToCacheMap = new HashMap<>();

    private static EventsIpResolvingService service;


    private final static String topicConfigKeyFormat = "fortscale.%s.topic";
    private final static String storeConfigKeyFormat = "fortscale.%s.store";

    private final static String dhcpCacheKey = "dhcp-cache";
    private final static String loginCacheKey = "login-cache";
    private final static String computerCacheKey = "computer-cache";


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {

        // initialize the ip resolving service only once for all streaming task instances. Since we can
        // host several task instances in this process, we want all of them to share the same ip resolving cache
        // instances. To do so, we can have the events ip resolving service defined as a static member and be shared
        // for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
        // threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
        // init or process methods here, so it is safe to check for initialization the way we did.
        if (service==null) {

            IpToHostnameResolver resolver = SpringService.getInstance().resolve(IpToHostnameResolver.class);

            // create leveldb based caches for ip resolving services (dhcp, login, computer) and pass the caches to the ip resolving services
            LevelDbBasedCache<String,DhcpEvent> dhcpCache = new LevelDbBasedCache<String, DhcpEvent>(
                    (KeyValueStore<String, DhcpEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dhcpCacheKey))),DhcpEvent.class);
            resolver.getDhcpResolver().setCache(dhcpCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, dhcpCacheKey)), resolver.getDhcpResolver());

            LevelDbBasedCache<String,ComputerLoginEvent> loginCache = new LevelDbBasedCache<String,ComputerLoginEvent>(
                    (KeyValueStore<String, ComputerLoginEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, loginCacheKey))),ComputerLoginEvent.class);
            resolver.getComputerLoginResolver().setCache(loginCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, loginCacheKey)), resolver.getComputerLoginResolver());

            LevelDbBasedCache<String, Computer> computerCache = new LevelDbBasedCache<String, Computer>((
                    KeyValueStore<String, Computer>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, computerCacheKey))), Computer.class);
            resolver.getComputerService().setCache(computerCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, computerCacheKey)), resolver.getComputerService());

            // get spring environment to resolve properties values using configuration files
            Environment env = SpringService.getInstance().resolve(Environment.class);

            // build EventResolvingConfig instances from streaming task configuration file
            List<EventResolvingConfig> resolvingConfigList = new LinkedList<>();
            Config fieldsSubset = config.subset("fortscale.events.");
            for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {
                String eventType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".input.topic"));

                // load configuration for event type
                String inputTopic = getConfigString(config, String.format("fortscale.events.%s.input.topic", eventType));
                String outputTopic = getConfigString(config, String.format("fortscale.events.%s.output.topic", eventType));
                String ipField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.ip.field", eventType)));
                String hostField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.host.field", eventType)));
                String timestampField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.timestamp.field", eventType)));
                boolean restrictToADName = config.getBoolean(String.format("fortscale.events.%s.restrictToADName", eventType));
                boolean shortName = config.getBoolean(String.format("fortscale.events.%s.shortName", eventType));
                boolean isRemoveLastDot = config.getBoolean(String.format("fortscale.events.%s.isRemoveLastDot", eventType));
				boolean dropWhenFail = config.getBoolean(String.format("fortscale.events.%s.dropWhenFail", eventType));
                String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.partition.field", eventType)));


                // build EventResolvingConfig for the event type
                resolvingConfigList.add(EventResolvingConfig.build(inputTopic, ipField, hostField, outputTopic,
                        restrictToADName, shortName, isRemoveLastDot,dropWhenFail, timestampField, partitionField));
            }

            // construct the resolving service
            service = new EventsIpResolvingService(resolver, resolvingConfigList);
        }
    }



    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        //if the message came from one of the cache updates topics, if so than update the resolving cache
        // with the update message
        String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
        if (topicToCacheMap.containsKey(topic)) {
            // get the concrete cache and pass it the update check  message that arrive
            CachingService cachingService = topicToCacheMap.get(topic);
            cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
        } else {
            // process event message
            String messageText = (String)envelope.getMessage();
            JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);

            event = service.enrichEvent(topic, event);


			//move to the next topic only if you are not event that need to drop
            //we are dropping only security events in the case the resolving is not successful.
            // we are doing so, to prevent cases of 4769 events from a machine to itself.
            // most of the cases we can't resolve the host name in 4769 events are self connect.
            //TODO: in next versions we want to add extra check if this is the case or not.
			if (!service.dropEvent(topic, event)) {

				// construct outgoing message
				try {
					OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(
							new SystemStream("kafka", service.getOutputTopic(topic)),
							service.getPartitionKey(topic, event),
							event.toJSONString());
					collector.send(output);
				} catch (Exception exception) {
					throw new KafkaPublisherException(String.format("failed to send event to from input topic %s, topic %s after ip resolving", topic, service.getOutputTopic(topic)), exception);
				}
			}
        }
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {}

    @Override
    protected void wrappedClose() throws Exception {
        // close all leveldb resolving caches
        for(CachingService cachingService: topicToCacheMap.values()) {
            cachingService.getCache().close();
        }
    }
}
