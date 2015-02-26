package fortscale.streaming.task.enrichment;

import static fortscale.streaming.ConfigUtils.getConfigString;
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
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.vpn.VpnDataBucketsConfig;
import fortscale.streaming.service.vpn.VpnEnrichConfig;
import fortscale.streaming.service.vpn.VpnEnrichService;
import fortscale.streaming.service.vpn.VpnGeolocationConfig;
import fortscale.streaming.service.vpn.VpnSessionUpdateConfig;
import fortscale.streaming.task.AbstractStreamTask;

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


    private VpnEnrichService vpnEnrichService;
    private String usernameFieldName;


    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        // init geolocation service:
        initGeolocation(config);

    }

    public VpnEnrichService getVpnEnrichService() {
        return vpnEnrichService;
    }

    public void setVpnEnrichService(VpnEnrichService vpnEnrichService) {
        this.vpnEnrichService = vpnEnrichService;
    }

    private void initGeolocation(Config config) {
        // get spring environment to resolve properties values using configuration files
        Environment env = SpringService.getInstance().resolve(Environment.class);

        String inputTopic = getConfigString(config, "task.inputs");
        String outputTopic = getConfigString(config, "fortscale.output.topic");
        String partitionField = env.getProperty(getConfigString(config, "fortscale.events.vpn.partition.field"));
        //geolocation field names:
        String ipField = env.getProperty(getConfigString(config, "fortscale.events.vpn.ip.field"));
        String countryFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.country.field"));
        String countryIsoCodeFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.countryIsoCode.field"));
        String regionFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.region.field"));
        String cityFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.city.field"));
        String ispFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.isp.field"));
        String usageTypeFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.usageType.field"));
        String longtitudeFieldName = getConfigString(config, "fortscale.events.vpn.longtitude.field");
        String latitudeFieldName = getConfigString(config, "fortscale.events.vpn.latitude.field");
        usernameFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.username.field"));
                //data buckets field names:
                String totalbytesFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.totalbytes.field"));
        String readbytesFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.readbytes.field"));
        String durationFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.duration.field"));
        String databucketFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.databucket.field"));
        //session update field names:
        String vpnGeoHoppingOpenSessionThresholdInHours = env.getProperty(getConfigString(config, "fortscale.events.vpn.geoHoppingOpenSessionThresholdInHours"));
        String vpnGeoHoppingCloseSessionThresholdInHours = env.getProperty(getConfigString(config, "fortscale.events.vpn.geoHoppingCloseSessionThresholdInHours"));
        String sessionIdFieldName = getConfigString(config, "fortscale.events.vpn.sessionid.field");
        String runGeoHoppingFieldName = getConfigString(config, "fortscale.events.vpn.runGeoHopping.field");
        String addSessionDataFieldName = env.getProperty(getConfigString(config, "fortscale.events.vpn.addSessionData.field"));


        VpnGeolocationConfig vpnGeolocationConfig = new VpnGeolocationConfig(ipField, countryFieldName, countryIsoCodeFieldName, regionFieldName, cityFieldName, ispFieldName, usageTypeFieldName, longtitudeFieldName, latitudeFieldName);
        VpnDataBucketsConfig vpnDataBucketsConfig = new VpnDataBucketsConfig(totalbytesFieldName, readbytesFieldName, durationFieldName, databucketFieldName);
        VpnSessionUpdateConfig vpnSessionUpdateConfig = new VpnSessionUpdateConfig(countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName,
                Integer.parseInt(vpnGeoHoppingOpenSessionThresholdInHours), Integer.parseInt(vpnGeoHoppingCloseSessionThresholdInHours),
                sessionIdFieldName, runGeoHoppingFieldName, addSessionDataFieldName);
        VpnEnrichConfig vpnEnrichConfig = new VpnEnrichConfig(inputTopic, outputTopic, partitionField, vpnGeolocationConfig, vpnDataBucketsConfig, vpnSessionUpdateConfig);
        vpnEnrichService = new VpnEnrichService(vpnEnrichConfig);
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        // parse the message into json
        String messageText = (String) envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
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
