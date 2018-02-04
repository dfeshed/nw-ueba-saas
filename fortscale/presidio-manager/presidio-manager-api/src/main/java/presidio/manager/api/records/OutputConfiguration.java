package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.ConfigurationValidatable;

import java.util.List;

public class OutputConfiguration extends createConfigurationAndStructureValidation implements ConfigurationValidatable {

    private ConfigurationValidatable forwarderConfiguration;

    private final String SYSLOG = "syslog";

    public OutputConfiguration(JsonNode node) {
        createConfiguration(node);
        if (forwarderConfiguration != null) {
            badParamsAddKeys(addPrefixToBadParams(SYSLOG, forwarderConfiguration.badParams()));
            missingParamsAddKeys(addPrefixToBadParams(SYSLOG, forwarderConfiguration.missingParams()));
        } else {
            missingParamsAddKeys(SYSLOG);
        }
        checkStructure();
    }

    public ConfigurationValidatable getSyslogConfiguration() {
        return forwarderConfiguration;
    }


    public void setForwarderConfiguration(ConfigurationValidatable forwarderConfiguration) {
        this.forwarderConfiguration = forwarderConfiguration;
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
                forwarderConfiguration != null &&
                forwarderConfiguration.isValid());
    }

    @Override
    void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case SYSLOG:
                setForwarderConfiguration(new SyslogConfigurationValidatable(value));
                break;
            default:
                badParamsAddKey(key);

        }
    }
}
