package fortscale.streaming.task.enrichment;

import fortscale.domain.core.Computer;
import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.IseEvent;
import fortscale.domain.events.PxGridIPEvent;
import fortscale.services.CachingService;
import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.cache.KeyValueDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import fortscale.streaming.service.ipresolving.EventsIpResolvingService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.streaming.task.monitor.MonitorMessaages;
import net.minidev.json.JSONObject;
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
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Streaming task that receive events from input streams and resolves ip addresses in each event according to a
 * configuration saying which fields are to be resolved and with which resolving configuration (e.g. only AD names, etc)
 */
public class IpResolvingStreamTask extends AbstractStreamTask {

    // map between input topic name and relevant resolving cache instance
    private static Map<String, CachingService> topicToCacheMap = new HashMap<>();

    private final static String topicConfigKeyFormat = "fortscale.%s.topic";
    private final static String storeConfigKeyFormat = "fortscale.%s.store";

    private final static String dhcpCacheKey = "dhcp-cache";
	private final static String iseCacheKey = "ise-cache";
    private final static String pxGridCacheKey = "pxgrid-cache";
    private final static String loginCacheKey = "login-cache";
    private final static String computerCacheKey = "computer-cache";

    private static EventsIpResolvingService ipResolvingService;

    private Map<StreamingTaskDataSourceConfigKey, EventResolvingConfig> dataSourceToConfigurationMap = new HashMap<>();
	private String vpnIpPoolUpdaterTopicName;


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {


		res = SpringService.getInstance().resolve(FortscaleValueResolver.class);

        // initialize the ip resolving service only once for all streaming task instances. Since we can
        // host several task instances in this process, we want all of them to share the same ip resolving cache
        // instances. To do so, we can have the events ip resolving service defined as a static member and be shared
        // for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
        // threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
        // init or process methods here, so it is safe to check for initialization the way we did.
        if (ipResolvingService==null) {

            IpToHostnameResolver resolver = SpringService.getInstance().resolve(IpToHostnameResolver.class);

            // create leveldb based caches for ip resolving services (dhcp, ise, login, computer) and pass the caches to the ip resolving services
            KeyValueDbBasedCache<String,DhcpEvent> dhcpCache = new KeyValueDbBasedCache<String, DhcpEvent>(
                    (KeyValueStore<String, DhcpEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dhcpCacheKey))),DhcpEvent.class);
            resolver.getDhcpResolver().setCache(dhcpCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, dhcpCacheKey)), resolver.getDhcpResolver());

            KeyValueDbBasedCache<String, IseEvent> iseCache = new KeyValueDbBasedCache<String,IseEvent>(
                    (KeyValueStore<String, IseEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, iseCacheKey))),IseEvent.class);
            resolver.getIseResolver().setCache(iseCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, iseCacheKey)), resolver.getIseResolver());

            KeyValueDbBasedCache<String, PxGridIPEvent> pxGridCache = new KeyValueDbBasedCache<String,PxGridIPEvent>(
                    (KeyValueStore<String, PxGridIPEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, pxGridCacheKey))),PxGridIPEvent.class);
            resolver.getPxGridResolver().setCache(pxGridCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, pxGridCacheKey)), resolver.getIseResolver());

            KeyValueDbBasedCache<String,ComputerLoginEvent> loginCache = new KeyValueDbBasedCache<String,ComputerLoginEvent>(
                    (KeyValueStore<String, ComputerLoginEvent>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, loginCacheKey))),ComputerLoginEvent.class);
            resolver.getComputerLoginResolver().setCache(loginCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, loginCacheKey)), resolver.getComputerLoginResolver());

            KeyValueDbBasedCache<String, Computer> computerCache = new KeyValueDbBasedCache<String, Computer>((
                    KeyValueStore<String, Computer>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, computerCacheKey))), Computer.class);
            resolver.getComputerService().setCache(computerCache);
            topicToCacheMap.put(getConfigString(config, String.format(topicConfigKeyFormat, computerCacheKey)), resolver.getComputerService());

            // get spring environment to resolve properties values using configuration files
            Environment env = SpringService.getInstance().resolve(Environment.class);

            boolean defaultResolveOnlyReservedIp = config.getBoolean("fortscale.events.resolveOnlyReserved");
            String reservedIpAddress = getConfigString(config, "fortscale.events.reservedIpAddress");

			 vpnIpPoolUpdaterTopicName = resolveStringValue(config, "fortscale.vpn.ip.pool.update.topic.name", res);


            for (Map.Entry<String,String> ConfigField : config.subset("fortscale.events.entry.name.").entrySet()) {
                String configKey = ConfigField.getValue();
                String dataSource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", configKey));
                String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", configKey));

                String outputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", configKey));
                String ipField = resolveStringValue(config, String.format("fortscale.events.entry.%s.ip.field", configKey), res);
                String hostField = resolveStringValue(config, String.format("fortscale.events.entry.%s.host.field", configKey), res);
                String timestampField = resolveStringValue(config, String.format("fortscale.events.entry.%s.timestamp.field", configKey), res);
                boolean restrictToADName = config.getBoolean(String.format("fortscale.events.entry.%s.restrictToADName", configKey));
                boolean shortName = config.getBoolean(String.format("fortscale.events.entry.%s.shortName", configKey));
                boolean isRemoveLastDot = config.getBoolean(String.format("fortscale.events.entry.%s.isRemoveLastDot", configKey));
                boolean dropWhenFail = config.getBoolean(String.format("fortscale.events.entry.%s.dropWhenFail", configKey));
                String partitionField = resolveStringValue(config, String.format("fortscale.events.entry.%s.partition.field", configKey), res);
                boolean overrideIPWithHostname = config.getBoolean(String.format("fortscale.events.entry.%s.overrideIPWithHostname", configKey));
                boolean eventTypeResolveOnlyReservedIp = config.getBoolean(String.format("fortscale.events.entry.%s.resolveOnlyReserved", configKey), defaultResolveOnlyReservedIp);

                // build EventResolvingConfig for the event type
                EventResolvingConfig eventResolvingConfig = EventResolvingConfig.build(dataSource, lastState, ipField, hostField, outputTopic,
                        restrictToADName, shortName, isRemoveLastDot, dropWhenFail, timestampField, partitionField,
                        overrideIPWithHostname, eventTypeResolveOnlyReservedIp, reservedIpAddress);

                dataSourceToConfigurationMap.put(new StreamingTaskDataSourceConfigKey(dataSource, lastState), eventResolvingConfig);
            }

            // construct the resolving service
            ipResolvingService = new EventsIpResolvingService(resolver, dataSourceToConfigurationMap, taskMonitoringHelper);
        }
    }



    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        //if the message came from one of the cache updates topics, if so than update the resolving cache
        // with the update message
        String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		//in case of vpn ip update - (session was closed and the ip was related to this session , we need to mark all the resolving for that ip in the period time of the session )
		if (topic.equals(vpnIpPoolUpdaterTopicName))
		{
			JSONObject message = parseJsonMessage(envelope);
			String ip = convertToString(message.get("ip"));
			ipResolvingService.removeIpFromCache(ip);

		}

        else if (topicToCacheMap.containsKey(topic)) {
            // get the concrete cache and pass it the update check  message that arrive
            CachingService cachingService = topicToCacheMap.get(topic);
            cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
        } else {
            JSONObject message = parseJsonMessage(envelope);

            StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
            if (configKey == null){
                taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.BAD_CONFIG_KEY);
                return;
            }

            EventResolvingConfig eventResolvingConfig = dataSourceToConfigurationMap.get(configKey);

            if (eventResolvingConfig == null){
                taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.NO_STATE_CONFIGURATION_MESSAGE);
                return;
            }

            message = ipResolvingService.enrichEvent(eventResolvingConfig, message);

            //move to the next topic only if you are not message that need to drop
            //we are dropping only security events in the case the resolving is not successful.
            // we are doing so, to prevent cases of 4769 events from a machine to itself.
            // most of the cases we can't resolve the host name in 4769 events are self connect.
            //TODO: in next versions we want to add extra check if this is the case or not.
            if (!ipResolvingService.filterEventIfNeeded(eventResolvingConfig,message,configKey)) {

                try {
                    OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(
                            new SystemStream(KAFKA_MESSAGE_QUEUE, ipResolvingService.getOutputTopic(configKey)),
                            ipResolvingService.getPartitionKey(configKey, message),
                            message.toJSONString());
                    handleUnfilteredEvent(message,configKey);
                    collector.send(output);
                } catch (Exception exception) {
                    throw new KafkaPublisherException(String.format("failed to send message %s from input topic %s to output topic %s", message.toJSONString(), topic, ipResolvingService.getOutputTopic(configKey)), exception);
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

    @Override
    protected String getJobLabel() {
        return "IpResolvingStreamTask";
    }
}
