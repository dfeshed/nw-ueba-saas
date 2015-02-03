package fortscale.streaming.task.enrichment;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.vpn.VpnDataBucketsConfig;
import fortscale.streaming.service.vpn.VpnEnrichConfig;
import fortscale.streaming.service.vpn.VpnEnrichService;
import fortscale.streaming.service.vpn.VpnGeolocationConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToString;

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

        String inputTopic = getConfigString(config, String.format("task.inputs"));
        String outputTopic = getConfigString(config, String.format("fortscale.output.topic"));
        String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.partition.field")));
        //geolocation field names:
        String ipField = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.ip.field")));
        String countryFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.country.field")));
        String countryIsoCodeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.countryIsoCode.field")));
        String regionFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.region.field")));
        String cityFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.city.field")));
        String ispFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.isp.field")));
        String usageTypeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.usageType.field")));
        String longtitudeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.longtitude.field")));
        String latitudeFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.latitude.field")));
        //data buckets field names:
        String totalbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.totalbytes.field")));
        String readbytesFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.readbytes.field")));
        String durationFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.duration.field")));
        String databucketFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.vpn.databucket.field")));

        VpnGeolocationConfig vpnGeolocationConfig = new VpnGeolocationConfig(ipField, countryFieldName, countryIsoCodeFieldName, regionFieldName, cityFieldName, ispFieldName, usageTypeFieldName, longtitudeFieldName, latitudeFieldName);
        VpnDataBucketsConfig vpnDataBucketsConfig = new VpnDataBucketsConfig(totalbytesFieldName, readbytesFieldName, durationFieldName, databucketFieldName);
        VpnEnrichConfig vpnEnrichConfig = new VpnEnrichConfig(inputTopic, outputTopic, partitionField, vpnGeolocationConfig, vpnDataBucketsConfig);
        vpnEnrichService = new VpnEnrichService(vpnEnrichConfig);
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        // parse the message into json
        String messageText = (String) envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
        message = vpnEnrichService.processVpnEvent(message);
        try {
            OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", vpnEnrichService.getOutputTopic()), vpnEnrichService.getPartitionKey(message), message.toJSONString());
            collector.send(output);
        } catch (Exception exception) {
            throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after VPN Enrich", vpnEnrichService.getInputTopic(), vpnEnrichService.getOutputTopic()), exception);
        }
    }




    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

    }

    @Override
    protected void wrappedClose() throws Exception {

    }
}
