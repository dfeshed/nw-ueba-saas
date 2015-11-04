package fortscale.streaming.task.enrichment;

import fortscale.domain.core.Computer;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.IseEvent;
import fortscale.services.CachingService;
import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import fortscale.streaming.service.ipresolving.EventsIpResolvingService;
import fortscale.streaming.task.AbstractStreamTask;
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
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Streaming task that receive events from input streams and resolves ip addresses in each event according to a
 * configuration saying which fields are to be resolved and with which resolving configuration (e.g. only AD names, etc)
 */
public class IpResolvingStreamTask extends AbstractStreamTask {

    private static Logger logger = LoggerFactory.getLogger(IpResolvingStreamTask.class);

    // map between input topic name and relevant resolving cache instance
    private static Map<String, CachingService> inputTopicToCachingServiceMap = new HashMap<>();

    private static EventsIpResolvingService eventsIpResolvingService;

    private final static String topicConfigKeyFormat = "fortscale.%s.topic";
    private final static String storeConfigKeyFormat = "fortscale.%s.store";

    private final static String dhcpCacheKey = "dhcp-cache";
	private final static String iseCacheKey = "ise-cache";
    private final static String loginCacheKey = "login-cache";
    private final static String computerCacheKey = "computer-cache";

    private static final String DATA_SOURCE_FIELD = "dataSource";


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {

        // initialize the ip resolving service only once for all streaming task instances. Since we can
        // host several task instances in this process, we want all of them to share the same ip resolving cache
        // instances. To do so, we can have the events ip resolving service defined as a static member and be shared
        // for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
        // threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
        // init or process methods here, so it is safe to check for initialization the way we did.
        if (eventsIpResolvingService ==null) {

            IpToHostnameResolver resolver = SpringService.getInstance().resolve(IpToHostnameResolver.class);

            // create leveldb based caches for ip resolving services (dhcp, ise, login, computer) and pass the caches to the ip resolving services
            LevelDbBasedCache<String,DhcpEvent> dhcpCache = new LevelDbBasedCache<String, DhcpEvent>(
                    (KeyValueStore<String, DhcpEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dhcpCacheKey))),DhcpEvent.class);
            resolver.getDhcpResolver().setCache(dhcpCache);
            inputTopicToCachingServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, dhcpCacheKey)), resolver.getDhcpResolver());

            LevelDbBasedCache<String, IseEvent> iseCache = new LevelDbBasedCache<String,IseEvent>(
                    (KeyValueStore<String, IseEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, iseCacheKey))),IseEvent.class);
            resolver.getIseResolver().setCache(iseCache);
            inputTopicToCachingServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, iseCacheKey)), resolver.getIseResolver());

            LevelDbBasedCache<String,ComputerLoginEvent> loginCache = new LevelDbBasedCache<String,ComputerLoginEvent>(
                    (KeyValueStore<String, ComputerLoginEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, loginCacheKey))),ComputerLoginEvent.class);
            resolver.getComputerLoginResolver().setCache(loginCache);
            inputTopicToCachingServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, loginCacheKey)), resolver.getComputerLoginResolver());

            LevelDbBasedCache<String, Computer> computerCache = new LevelDbBasedCache<String, Computer>((
                    KeyValueStore<String, Computer>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, computerCacheKey))), Computer.class);
            resolver.getComputerService().setCache(computerCache);
            inputTopicToCachingServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, computerCacheKey)), resolver.getComputerService());

            // get spring environment to resolve properties values using configuration files
            Environment env = SpringService.getInstance().resolve(Environment.class);

            boolean defaultResolveOnlyReservedIp = config.getBoolean("fortscale.events.resolveOnlyReserved");
            String reservedIpAddress = getConfigString(config, "fortscale.events.reservedIpAddress");

            // build EventResolvingConfig instances from streaming task configuration file
            List<EventResolvingConfig> resolvingConfigList = new LinkedList<>();
            Config fieldsSubset = config.subset("fortscale.events.");

            for (Map.Entry<String,String> ConfigField : config.subset("fortscale.events.data.source.").entrySet()) {
                String dataSource = ConfigField.getKey();

                String inputTopic = getConfigString(config, String.format("fortscale.events.input.topic.%s", dataSource));
                String outputTopic = getConfigString(config, String.format("fortscale.events.output.topic.%s", dataSource));
                String ipField = env.getProperty(getConfigString(config, String.format("fortscale.events.ip.field.%s", dataSource)));
                String hostField = env.getProperty(getConfigString(config, String.format("fortscale.events.host.field.%s", dataSource)));
                String timestampField = env.getProperty(getConfigString(config, String.format("fortscale.events.timestamp.field.%s", dataSource)));
                boolean restrictToADName = config.getBoolean(String.format("fortscale.events.restrictToADName.%s", dataSource));
                boolean shortName = config.getBoolean(String.format("fortscale.events.shortName.%s", dataSource));
                boolean isRemoveLastDot = config.getBoolean(String.format("fortscale.events.isRemoveLastDot.%s", dataSource));
				boolean dropWhenFail = config.getBoolean(String.format("fortscale.events.dropWhenFail.%s", dataSource));
                String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.partition.field.%s", dataSource)));
                boolean overrideIPWithHostname = config.getBoolean(String.format("fortscale.events.overrideIPWithHostname.%s", dataSource));
                boolean dataSourceResolveOnlyReservedIp = config.getBoolean(String.format("fortscale.events.resolveOnlyReserved.%s", dataSource),defaultResolveOnlyReservedIp);



                // build EventResolvingConfig for the event type
                resolvingConfigList.add(EventResolvingConfig.build(dataSource, inputTopic, ipField, hostField, outputTopic,
                        restrictToADName, shortName, isRemoveLastDot, dropWhenFail, timestampField, partitionField,
                        overrideIPWithHostname,dataSourceResolveOnlyReservedIp, reservedIpAddress));
            }

            // construct the resolving service
            eventsIpResolvingService = new EventsIpResolvingService(resolver, resolvingConfigList);
        }
    }



    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        //if the message came from one of the cache updates topics, if so than update the resolving cache
        // with the update message
        String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

        if (inputTopicToCachingServiceMap.containsKey(inputTopic)) {
            // get the concrete cache and pass it the update check  message that arrive
            CachingService cachingService = inputTopicToCachingServiceMap.get(inputTopic);
            cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
        } else {
            String messageText = (String)envelope.getMessage();

            JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

            String dataSource = convertToString(message.get(DATA_SOURCE_FIELD));

            if (dataSource == null) {
                logger.error("Could not find mandatory dataSource field. Skipping message: {} ", messageText);

                return;
            }

			//move to the next inputTopic only if you are not event that need to drop
            //we are dropping only security events in the case the resolving is not successful.
            // we are doing so, to prevent cases of 4769 events from a machine to itself.
            // most of the cases we can't resolve the host name in 4769 events are self connect.
            //TODO: in next versions we want to add extra check if this is the case or not.
			if (!eventsIpResolvingService.dropEvent(dataSource, message)) {

				// construct outgoing message
				try {
					OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(
							new SystemStream("kafka", eventsIpResolvingService.getOutputTopic(dataSource)),
							eventsIpResolvingService.getPartitionKey(dataSource, message),
                            message.toJSONString());
					collector.send(output);
				} catch (Exception exception) {
					throw new KafkaPublisherException(String.format("failed to send event to from input inputTopic %s, inputTopic %s after ip resolving", inputTopic, eventsIpResolvingService.getOutputTopic(dataSource)), exception);
				}
			}
        }
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {}

    @Override
    protected void wrappedClose() throws Exception {
        // close all leveldb resolving caches
        for(CachingService cachingService: inputTopicToCachingServiceMap.values()) {
            cachingService.getCache().close();
        }
    }
}
