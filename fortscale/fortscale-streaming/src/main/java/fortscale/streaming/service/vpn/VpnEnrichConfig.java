package fortscale.streaming.service.vpn;

import fortscale.streaming.service.tagging.computer.ComputerTaggingFieldsConfig;

import java.util.List;

/**
 * Configuration for VPN geolocation. This should be constructed from the streaming task configuration and passed to the VpnEnrichService.
 */
public class VpnEnrichConfig {

    private String inputTopic;
    private String outputTopic;
    private String partitionField;

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

    public VpnEnrichConfig(String inputTopic, String outputTopic, String partitionField, VpnGeolocationConfig
            vpnGeolocationConfig, VpnDataBucketsConfig vpnDataBucketsConfig, VpnSessionUpdateConfig
            vpnSessionUpdateConfig, String usernameFieldName) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.partitionField = partitionField;
        this.vpnGeolocationConfig = vpnGeolocationConfig;
        this.vpnDataBucketsConfig = vpnDataBucketsConfig;
        this.vpnSessionUpdateConfig = vpnSessionUpdateConfig;
        this.usernameFieldName = usernameFieldName;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
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
}
