package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import presidio.manager.api.configuration.PresidioForwarderConfiguration;

import java.util.ArrayList;

public class OutputConfiguration extends createConfigurationAndStructureValidiation {

    private PresidioForwarderConfiguration forwarderConfiguration;

    private final String SYSLOG = "syslog";

    public OutputConfiguration(JsonNode node) {
        setBadParams(new ArrayList<>());
        createConfiguration(node);
        badParamsAddKeys(addPrefixToBadParams(SYSLOG, forwarderConfiguration.badParams()));
        checkStructure();
    }

    public PresidioForwarderConfiguration getSyslogConfiguration() {
        return forwarderConfiguration;
    }


    public void setForwarderConfiguration(PresidioForwarderConfiguration forwarderConfiguration) {
        this.forwarderConfiguration = forwarderConfiguration;
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
                setForwarderConfiguration(new SyslogConfiguration(value));
                break;
            default:
                badParamsAddKey(key);

        }
    }
}
