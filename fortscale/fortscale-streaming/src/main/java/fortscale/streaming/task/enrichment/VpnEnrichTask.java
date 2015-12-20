package fortscale.streaming.task.enrichment;

import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.FortscaleStringValueResolver;
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
		res = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);

        initGeolocation(config);

    }


    private void initGeolocation(Config config) {

		if (dataSourceConfigs == null) {

			dataSourceConfigs = new HashMap<>();



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
				String usernameFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s" + ".username.field", dsSettings),res);
				String longtitudeFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.longtitude.field", dsSettings));
				String latitudeFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.latitude.field", dsSettings));
				String countryIsoCodeFieldName =resolveStringValue(config, String.format("fortscale.events.entry.%s.countryIsoCode.field", dsSettings),res);

				VpnGeolocationConfig vpnGeolocationConfig = null;
				VpnDataBucketsConfig vpnDataBucketsConfig = null;
				VpnSessionUpdateConfig vpnSessionUpdateConfig = null;

				if (doGeoLocationh) {
					//geolocation field names:
					String ipField = resolveStringValue(config, String.format("fortscale.events.entry.%s.ip.field", dsSettings),res);
					String countryFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.country.field", dsSettings),res);

					String regionFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.region.field", dsSettings),res);
					String cityFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.city.field", dsSettings),res);
					String ispFieldName =resolveStringValue(config, String.format("fortscale.events.entry.%s.isp.field", dsSettings),res);
					String usageTypeFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.usageType.field", dsSettings),res);

					vpnGeolocationConfig = new VpnGeolocationConfig(ipField, countryFieldName, countryIsoCodeFieldName,regionFieldName, cityFieldName, ispFieldName, usageTypeFieldName, longtitudeFieldName, latitudeFieldName);
				}


				if (doDataBuckets) {


					//data buckets field names:
					String totalbytesFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.totalbytes.field", dsSettings),res);
					String readbytesFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.readbytes.field", dsSettings),res);
					String durationFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.duration.field", dsSettings),res);
					String databucketFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.databucket.field", dsSettings),res);

					vpnDataBucketsConfig = new VpnDataBucketsConfig(totalbytesFieldName, readbytesFieldName, durationFieldName, databucketFieldName);
				}

				if (doSessionUpdate) {

					//session update field names:
					String vpnGeoHoppingOpenSessionThresholdInHours = resolveStringValue(config, String.format("fortscale.events.entry.%s.geoHoppingOpenSessionThresholdInHours", dsSettings),res);
					String vpnGeoHoppingCloseSessionThresholdInHours =resolveStringValue(config, String.format("fortscale.events.entry.%s.geoHoppingCloseSessionThresholdInHours", dsSettings),res);
					String sessionIdFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.sessionid.field", dsSettings));
					String runGeoHoppingFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.runGeoHopping.field", dsSettings));
					String addSessionDataFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.addSessionData.field", dsSettings),res);
					String resolveIpFieldName = getConfigString(config, String.format("fortscale.events.entry.%s.resolveIp.field", dsSettings));
					String dropCloseEventWhenOpenMissingFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.dropCloseEventWhenOpenMissing.field", dsSettings),res);
					String timeGapForResolveIpFrom = resolveStringValue(config, String.format("fortscale.events.entry.%s.timeGapForResolveIpFrom", dsSettings),res);
					String timeGapForResolveIpTo = resolveStringValue(config, String.format("fortscale.events.entry.%s.timeGapForResolveIpTo", dsSettings),res);


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

		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
		if (configKey == null){
			taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, CANNOT_EXTRACT_STATE_MESSAGE);
			return;
		}
		VpnEnrichService vpnEnrichService = dataSourceConfigs.get(configKey);

        message = vpnEnrichService.processVpnEvent(message, collector);

		String usernameFieldName = vpnEnrichService.getUsernameFieldName();

        if(message.get(usernameFieldName) == null || message.get(usernameFieldName).equals("")){
			taskMonitoringHelper.countNewFilteredEvents(configKey, super.CANNOT_EXTRACT_USER_NAME_MESSAGE);
            logger.error("No username field in event {}. Dropping Record", messageText);
            return;
        }
        try {
            OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", vpnEnrichService.getOutputTopic()), vpnEnrichService.getPartitionKey(message), message.toJSONString());
            collector.send(output);
			handleUnfilteredEvent(message, configKey);
        } catch (Exception exception) {
			taskMonitoringHelper.countNewFilteredEvents(configKey, super.SEND_TO_OUTPUT_TOPIC_FAILED_MESSAGE);
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
