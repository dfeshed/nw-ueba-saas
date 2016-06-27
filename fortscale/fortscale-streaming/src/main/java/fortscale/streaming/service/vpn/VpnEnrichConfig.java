package fortscale.streaming.service.vpn;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.vpn.metrics.VpnEnrichServiceMetrics;
import fortscale.utils.monitoring.stats.StatsService;

/**
 * Configuration for VPN geolocation. This should be constructed from the streaming task configuration and passed to the VpnEnrichService.
 */
public class VpnEnrichConfig {

    private StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey;
    private String outputTopic;
    private String partitionField;
    private VpnEnrichServiceMetrics metrics;

    public String getUsernameFieldName() {
        return usernameFieldName;
    }

    public void setUsernameFieldName(String usernameFieldName) {
        this.usernameFieldName = usernameFieldName;
    }

    private String usernameFieldName;

    private VpnGeolocationConfig vpnGeolocationConfig;
    private VpnDataBucketsConfig vpnDataBucketsConfig;
    private VpnSessionUpdateConfig vpnSessionUpdateConfig;

    public VpnEnrichConfig(StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey, String outputTopic, String partitionField, VpnGeolocationConfig
            vpnGeolocationConfig, VpnDataBucketsConfig vpnDataBucketsConfig, VpnSessionUpdateConfig
                                   vpnSessionUpdateConfig, String usernameFieldName, StatsService statsService) {
        this.streamingTaskDataSourceConfigKey = streamingTaskDataSourceConfigKey;
        this.outputTopic = outputTopic;
        this.partitionField = partitionField;
        this.vpnGeolocationConfig = vpnGeolocationConfig;
        this.vpnDataBucketsConfig = vpnDataBucketsConfig;
        this.vpnSessionUpdateConfig = vpnSessionUpdateConfig;
        this.usernameFieldName = usernameFieldName;
        this.metrics = new VpnEnrichServiceMetrics(statsService, streamingTaskDataSourceConfigKey);
    }

    public StreamingTaskDataSourceConfigKey getStreamingTaskDataSourceConfigKey() {
        return streamingTaskDataSourceConfigKey;
    }

    public void setStreamingTaskDataSourceConfigKey(StreamingTaskDataSourceConfigKey streamingTaskDataSourceConfigKey) {
        this.streamingTaskDataSourceConfigKey = streamingTaskDataSourceConfigKey;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getPartitionField() {
        return partitionField;
    }

    public void setPartitionField(String partitionField) {
        this.partitionField = partitionField;
    }

    public VpnGeolocationConfig getVpnGeolocationConfig() {
        return vpnGeolocationConfig;
    }

    public void setVpnGeolocationConfig(VpnGeolocationConfig vpnGeolocationConfig) {
        this.vpnGeolocationConfig = vpnGeolocationConfig;
    }

    public VpnDataBucketsConfig getVpnDataBucketsConfig() {
        return vpnDataBucketsConfig;
    }

    public void setVpnDataBucketsConfig(VpnDataBucketsConfig vpnDataBucketsConfig) {
        this.vpnDataBucketsConfig = vpnDataBucketsConfig;
    }

    public VpnSessionUpdateConfig getVpnSessionUpdateConfig() {
        return vpnSessionUpdateConfig;
    }

    public void setVpnSessionUpdateConfig(VpnSessionUpdateConfig vpnSessionUpdateConfig) {
        this.vpnSessionUpdateConfig = vpnSessionUpdateConfig;
    }

    public VpnEnrichServiceMetrics getMetrics() {
        return metrics;
    }
}
