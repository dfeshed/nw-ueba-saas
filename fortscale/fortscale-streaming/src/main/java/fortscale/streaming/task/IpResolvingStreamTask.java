package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.*;

import com.google.common.collect.Iterables;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.ipresolving.*;
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

/**
 * Streaming task that receive events from input streams and resolves ip addresses in each event according to a
 * configuration saying which fields are to be resolved and with which resolving configuration (e.g. only AD names, etc)
 */
public class IpResolvingStreamTask extends AbstractStreamTask {

    // map between input topic name and relevant resolving cache instance
    private Map<String, LevelDbBasedResolvingCache<?>> topicToCacheMap = new HashMap<>();

    private static EventsIpResolvingService service;


    private static String topicConfigKeyFormat = "fortscale.%s.topic";
    private static String storeConfigKeyFormat = "fortscale.%s.store";

    private static String dhcpCacheKey = "dhcp-cache";
    private static String loginCacheKey = "login-cache";


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {

        // initialize the ip resolving service only once for all streaming task instances. Since we can
        // host several task instances in this process, we want all of them to share the same ip resolving cache
        // instances. To do so, we can have the events ip resolving service defined as a static member and be shared
        // for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
        // threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
        // init or process methods here, so it is safe to check for initialization the way we did.
        if (service==null) {

            // create leveldb based caches for ip resolving services (dhcp, login)
            LevelDbBasedResolvingCache<DhcpEvent> dhcpCache = new DhcpLevelDbResolvingCache(
                    (KeyValueStore<String, DhcpEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dhcpCacheKey))));
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, dhcpCacheKey)), dhcpCache);

            LevelDbBasedResolvingCache<ComputerLoginEvent> loginCache = new ComputerLoginLevelDbResolvingCache(
                    (KeyValueStore<String, ComputerLoginEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, loginCacheKey))));
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, loginCacheKey)), loginCache);


            // pass the caches to the ip resolving services
            IpToHostnameResolver resolver = SpringService.getInstance().resolve(IpToHostnameResolver.class);
            resolver.getDhcpResolver().setCache(dhcpCache);
            resolver.getComputerLoginResolver().setCache(loginCache);

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
                String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.partition.field", eventType)));


                // build EventResolvingConfig for the event type
                resolvingConfigList.add(EventResolvingConfig.build(inputTopic, ipField, hostField, outputTopic,
                        restrictToADName, shortName, isRemoveLastDot, timestampField, partitionField));
            }

            // construct the resolving service
            service = new EventsIpResolvingService(resolver, resolvingConfigList);
        }
    }



    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        // check if the message came from one of the cache updates topics, if so than update the resolving cache
        // with the update message
        String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
        if (topicToCacheMap.containsKey(topic)) {
            // get the concrete cache and pass it the update message that arrive
            LevelDbBasedResolvingCache<?> cache = topicToCacheMap.get(topic);
            cache.update((String) envelope.getKey(), (String)envelope.getMessage());
        } else {
            // process event message
            String messageText = (String)envelope.getMessage();
            JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);

            event = service.enrichEvent(topic, event);

            // construct outgoing message
            try {
                OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(
                        new SystemStream("kafka", service.getOutputTopic(topic)),
                        service.getPartitionKey(topic, event),
                        event.toJSONString());
                collector.send(output);
            } catch(Exception exception){
                throw new KafkaPublisherException(String.format("failed to send event to from input topic %s, topic %s after ip resolving", topic, service.getOutputTopic(topic)), exception);
            }
        }
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {}

    @Override
    protected void wrappedClose() throws Exception {
        // close all leveldb resolving caches
        for (LevelDbBasedResolvingCache<?> cache : topicToCacheMap.values())
            cache.flush();
    }
}
