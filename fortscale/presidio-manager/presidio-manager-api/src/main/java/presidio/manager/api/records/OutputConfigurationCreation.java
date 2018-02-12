package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.ConfigurationValidatable;

import java.util.List;

public class OutputConfigurationCreation extends ConfigurationCreation implements ConfigurationValidatable {

    private ConfigurationValidatable syslogForwardingConfiguration;

    private final String SYSLOG = "syslog";


    public OutputConfigurationCreation() {
    }

    public OutputConfigurationCreation(JsonNode node) {
        createConfiguration(node);
        if (syslogForwardingConfiguration != null) {
            badParamsAddKeys(addPrefixToBadParams(SYSLOG, syslogForwardingConfiguration.badParams()));
            missingParamsAddKeys(addPrefixToBadParams(SYSLOG, syslogForwardingConfiguration.missingParams()));
        } else {
            missingParamsAddKeys(SYSLOG);
        }
        checkStructure();
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
            default:
                badParamsAddKey(key);

        }
    }
}
