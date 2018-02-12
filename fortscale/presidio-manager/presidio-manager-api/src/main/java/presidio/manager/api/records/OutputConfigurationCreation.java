package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.ConfigurationValidatable;

import java.util.List;

public class OutputConfigurationCreation extends ConfigurationCreation implements ConfigurationValidatable {

    private ConfigurationValidatable syslogForwardingConfiguration;
    private boolean isEnableForwarding;

    private final String SYSLOG = "syslog";
    private final String ENABLE_FORWARDING = "enableForwarding";


    public OutputConfigurationCreation() {
    }

    public OutputConfigurationCreation(JsonNode node) {
        createConfiguration(node);
        if (syslogForwardingConfiguration == null && !isEnableForwarding) {
            setStructureValid(true);
        } else {
            if (syslogForwardingConfiguration != null && isEnableForwarding) {
                badParamsAddKeys(addPrefixToBadParams(SYSLOG, syslogForwardingConfiguration.badParams()));
                missingParamsAddKeys(addPrefixToBadParams(SYSLOG, syslogForwardingConfiguration.missingParams()));
            } else {
                if (syslogForwardingConfiguration == null) {
                    missingParamsAddKeys(SYSLOG);
                } else {
                    setStructureValid(false);
                    badParamsAddKey(ENABLE_FORWARDING);
                }
            }
            checkStructure();
        }
    }

    public ConfigurationValidatable getSyslogForwardingConfiguration() {
        return syslogForwardingConfiguration;
    }


    public void setSyslogForwardingConfiguration(ConfigurationValidatable syslogForwardingConfiguration) {
        this.syslogForwardingConfiguration = syslogForwardingConfiguration;
    }

    @Override
    public boolean isValid() {
        return isStructureValid();
    }

    @Override
    public List<String> badParams() {
        return getBadParams();
    }

    @Override
    public List<String> missingParams() {
        return getMissingParams();
    }

    @Override
    void checkStructure() {
        setStructureValid(isStructureValid() &&
                syslogForwardingConfiguration != null &&
                syslogForwardingConfiguration.isValid());
    }

    @Override
    void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case SYSLOG:
                setSyslogForwardingConfiguration(new SyslogForwardingConfigurationCreation(value));
                break;
            case ENABLE_FORWARDING:
                if (Boolean.parseBoolean(value.asText())) {
                    isEnableForwarding = true;
                } else {
                    isEnableForwarding = false;
                }
                break;
            default:
                badParamsAddKey(key);

        }
    }
}
