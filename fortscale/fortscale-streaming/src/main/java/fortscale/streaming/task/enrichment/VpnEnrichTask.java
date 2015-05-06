package fortscale.streaming.task.enrichment;

import com.google.common.collect.Iterables;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.vpn.*;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.StringPredicates;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
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
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by rans on 01/02/15.
 *
 * Create a streaming task for VPN specific logic:
 * - calculate data buckets
 * - vpn session update (geo-hopping)
 * - vpn geolocation
 *
 */
public class VpnEnrichTask extends AbstractStreamTask {


    private static Logger logger = LoggerFactory.getLogger(VpnEnrichTask.class);



	// Map between (update) input topic name and relevant enrich service
	protected static Map<String, VpnEnrichService> topicToServiceMap;

    private String usernameFieldName;


	public static void setTopicToServiceMap(Map<String, VpnEnrichService> topicToServiceMap) {
		VpnEnrichTask.topicToServiceMap = topicToServiceMap;
	}



    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        // init geolocation service:
        initGeolocation(config);

    }


    private void initGeolocation(Config config) {

		if (topicToServiceMap == null) {

			topicToServiceMap = new HashMap<>();


			// get spring environment to resolve properties values using configuration files
			Environment env = SpringService.getInstance().resolve(Environment.class);

			Config configSubset = config.subset("fortscale.events.");

			for (String configKey : Iterables.filter(configSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {

				String eventType = configKey.substring(0, configKey.indexOf(".input.topic"));

				String inputTopic = getConfigString(config, String.format("fortscale.events.%s.input.topic", eventType));
				String outputTopic = getConfigString(config, String.format("fortscale.events.%s.output.topic", eventType));
				String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.partition.field", eventType)));

				Boolean doGeoLocationh = config.getBoolean(String.format("fortscale.events.%s.doGeoLocationh", eventType));
				Boolean doDataBuckets = config.getBoolean(String.format("fortscale.events.%s.doDataBuckets", eventType));
				Boolean doSessionUpdate = config.getBoolean(String.format("fortscale.events.%s.doSessionUpdate", eventType));
				usernameFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.username.field", eventType)));
				String longtitudeFieldName = getConfigString(config, String.format("fortscale.events.%s.longtitude.field", eventType));
				String latitudeFieldName = getConfigString(config, String.format("fortscale.events.%s.latitude.field", eventType));
				String countryIsoCodeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.countryIsoCode.field", eventType)));

				VpnGeolocationConfig vpnGeolocationConfig = null;
				VpnDataBucketsConfig vpnDataBucketsConfig = null;
				VpnSessionUpdateConfig vpnSessionUpdateConfig = null;

				if (doGeoLocationh) {
					//geolocation field names:
					String ipField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.ip.field", eventType)));
					String countryFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.country.field", eventType)));

					String regionFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.region.field", eventType)));
					String cityFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.city.field", eventType)));
					String ispFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.isp.field", eventType)));
					String usageTypeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.usageType.field", eventType)));

					vpnGeolocationConfig = new VpnGeolocationConfig(ipField, countryFieldName, countryIsoCodeFieldName,regionFieldName, cityFieldName, ispFieldName, usageTypeFieldName, longtitudeFieldName, latitudeFieldName);
				}


				if (doDataBuckets) {


					//data buckets field names:
					String totalbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.totalbytes.field", eventType)));
					String readbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.readbytes.field", eventType)));
					String durationFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.duration.field", eventType)));
					String databucketFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.databucket.field", eventType)));

					vpnDataBucketsConfig = new VpnDataBucketsConfig(totalbytesFieldName, readbytesFieldName, durationFieldName, databucketFieldName);
				}

				if (doSessionUpdate) {

					//session update field names:
					String vpnGeoHoppingOpenSessionThresholdInHours = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.geoHoppingOpenSessionThresholdInHours", eventType)));
					String vpnGeoHoppingCloseSessionThresholdInHours = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.geoHoppingCloseSessionThresholdInHours", eventType)));
					String sessionIdFieldName = getConfigString(config, String.format("fortscale.events.%s.sessionid.field", eventType));
					String runGeoHoppingFieldName = getConfigString(config, String.format("fortscale.events.%s.runGeoHopping.field", eventType));
					String addSessionDataFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.addSessionData.field", eventType)));
					String resolveIpFieldName = getConfigString(config, String.format("fortscale.events.%s.resolveIp.field", eventType));
					String dropCloseEventWhenOpenMissingFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.dropCloseEventWhenOpenMissing.field", eventType)));
					String timeGapForResolveIpFrom = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.timeGapForResolveIpFrom", eventType)));
					String timeGapForResolveIpTo = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.timeGapForResolveIpTo", eventType)));


					vpnSessionUpdateConfig = new VpnSessionUpdateConfig(countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName,
							Integer.parseInt(vpnGeoHoppingOpenSessionThresholdInHours), Integer.parseInt(vpnGeoHoppingCloseSessionThresholdInHours),
							sessionIdFieldName, runGeoHoppingFieldName, addSessionDataFieldName, resolveIpFieldName, dropCloseEventWhenOpenMissingFieldName, Long.parseLong(timeGapForResolveIpFrom), Long.parseLong(timeGapForResolveIpTo));

				}

				VpnEnrichConfig vpnEnrichConfig = new VpnEnrichConfig(inputTopic, outputTopic, partitionField, vpnGeolocationConfig, vpnDataBucketsConfig, vpnSessionUpdateConfig);
				VpnEnrichService vpnEnrichService = new VpnEnrichService(vpnEnrichConfig);

				topicToServiceMap.put(inputTopic, vpnEnrichService);
			}
		}

    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

        // parse the message into json
        String messageText = (String) envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		VpnEnrichService vpnEnrichService = topicToServiceMap.get(inputTopic);

        message = vpnEnrichService.processVpnEvent(message);

        if(message.get(usernameFieldName) == null || message.get(usernameFieldName).equals("")){
            logger.error("No username field in event {}. Dropping Record", messageText);
            return;
        }
        try {
            OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", vpnEnrichService.getOutputTopic()), vpnEnrichService.getPartitionKey(message), message.toJSONString());
            collector.send(output);
        } catch (Exception exception) {
            throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after VPN Enrich", vpnEnrichService.getInputTopic(), vpnEnrichService.getOutputTopic()), exception);
        }
    }


    public void setUsernameFieldName(String usernameFieldName) {
        this.usernameFieldName = usernameFieldName;
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

    }

    @Override
    protected void wrappedClose() throws Exception {

    }
}
