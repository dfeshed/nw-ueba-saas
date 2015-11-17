package fortscale.streaming.task.enrichment;

import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.vpn.*;
import fortscale.streaming.task.AbstractStreamTask;
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

public class VpnEnrichTask extends AbstractStreamTask  {


    private static Logger logger = LoggerFactory.getLogger(VpnEnrichTask.class);

	// Map between (update) input topic name and relevant enrich service
	protected static Map<StreamingTaskDataSourceConfigKey, VpnEnrichService> dataSourceConfigs;

	public static void setDataSourceConfigs(Map<StreamingTaskDataSourceConfigKey, VpnEnrichService> dataSourceConfigs) {
		VpnEnrichTask.dataSourceConfigs = dataSourceConfigs;
	}



    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        // init geolocation service:
        initGeolocation(config);

    }


    private void initGeolocation(Config config) {

		if (dataSourceConfigs == null) {

			dataSourceConfigs = new HashMap<>();


			// get spring environment to resolve properties values using configuration files
			Environment env = SpringService.getInstance().resolve(Environment.class);

			Config configSubset = config.subset("fortscale.events.entry.name.");

			for (String dsSettings : configSubset.keySet()) {

				String datasource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", dsSettings));
				String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", dsSettings));
				StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(datasource,lastState);
				String outputTopic  = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", dsSettings));
				String partitionField  = getConfigString(config, String.format("fortscale.events.entry.%s.partition.field", dsSettings));

				Boolean doGeoLocationh = config.getBoolean(String.format("fortscale.events.entry.%s.doGeoLocation", dsSettings));
				Boolean doDataBuckets = config.getBoolean(String.format("fortscale.events.entry.%s.doDataBuckets", dsSettings));
				Boolean doSessionUpdate = config.getBoolean(String.format("fortscale.events.entry.%s.doSessionUpdate", dsSettings));
				String usernameFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s" + ".username.field", dsSettings)));
				String longtitudeFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.longtitude.field", dsSettings));
				String latitudeFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.latitude.field", dsSettings));
				String countryIsoCodeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.countryIsoCode.field", dsSettings)));

				VpnGeolocationConfig vpnGeolocationConfig = null;
				VpnDataBucketsConfig vpnDataBucketsConfig = null;
				VpnSessionUpdateConfig vpnSessionUpdateConfig = null;

				if (doGeoLocationh) {
					//geolocation field names:
					String ipField = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.ip.field", dsSettings)));
					String countryFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.country.field", dsSettings)));

					String regionFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.region.field", dsSettings)));
					String cityFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.city.field", dsSettings)));
					String ispFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.isp.field", dsSettings)));
					String usageTypeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.usageType.field", dsSettings)));

					vpnGeolocationConfig = new VpnGeolocationConfig(ipField, countryFieldName, countryIsoCodeFieldName,regionFieldName, cityFieldName, ispFieldName, usageTypeFieldName, longtitudeFieldName, latitudeFieldName);
				}


				if (doDataBuckets) {


					//data buckets field names:
					String totalbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.totalbytes.field", dsSettings)));
					String readbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.readbytes.field", dsSettings)));
					String durationFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.duration.field", dsSettings)));
					String databucketFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.databucket.field", dsSettings)));

					vpnDataBucketsConfig = new VpnDataBucketsConfig(totalbytesFieldName, readbytesFieldName, durationFieldName, databucketFieldName);
				}

				if (doSessionUpdate) {

					//session update field names:
					String vpnGeoHoppingOpenSessionThresholdInHours = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.geoHoppingOpenSessionThresholdInHours", dsSettings)));
					String vpnGeoHoppingCloseSessionThresholdInHours = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.geoHoppingCloseSessionThresholdInHours", dsSettings)));
					String sessionIdFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.sessionid.field", dsSettings));
					String runGeoHoppingFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.runGeoHopping.field", dsSettings));
					String addSessionDataFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.addSessionData.field", dsSettings)));
					String resolveIpFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.resolveIp.field", dsSettings));
					String dropCloseEventWhenOpenMissingFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.dropCloseEventWhenOpenMissing.field", dsSettings)));
					String timeGapForResolveIpFrom = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.timeGapForResolveIpFrom", dsSettings)));
					String timeGapForResolveIpTo = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.timeGapForResolveIpTo", dsSettings)));


					vpnSessionUpdateConfig = new VpnSessionUpdateConfig(countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName,
							Integer.parseInt(vpnGeoHoppingOpenSessionThresholdInHours), Integer.parseInt(vpnGeoHoppingCloseSessionThresholdInHours),
							sessionIdFieldName, runGeoHoppingFieldName, addSessionDataFieldName, resolveIpFieldName, dropCloseEventWhenOpenMissingFieldName, Long.parseLong(timeGapForResolveIpFrom), Long.parseLong(timeGapForResolveIpTo));

				}

				VpnEnrichConfig vpnEnrichConfig = new VpnEnrichConfig(configKey, outputTopic, partitionField,
						vpnGeolocationConfig, vpnDataBucketsConfig, vpnSessionUpdateConfig, usernameFieldName);
				VpnEnrichService vpnEnrichService = new VpnEnrichService(vpnEnrichConfig);

				dataSourceConfigs.put(configKey, vpnEnrichService);
			}
		}

    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {


        // parse the message into json
        String messageText = (String) envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKey(message);
		VpnEnrichService vpnEnrichService = dataSourceConfigs.get(configKey);

        message = vpnEnrichService.processVpnEvent(message, collector);

		String usernameFieldName = vpnEnrichService.getUsernameFieldName();

        if(message.get(usernameFieldName) == null || message.get(usernameFieldName).equals("")){
            logger.error("No username field in event {}. Dropping Record", messageText);
            return;
        }
        try {
            OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", vpnEnrichService.getOutputTopic()), vpnEnrichService.getPartitionKey(message), message.toJSONString());
            collector.send(output);
        } catch (Exception exception) {
            throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after VPN Enrich", configKey, vpnEnrichService.getOutputTopic()), exception);
        }
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

    }

    @Override
    protected void wrappedClose() throws Exception {

    }
}