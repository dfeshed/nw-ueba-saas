package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class OutputConfiguration extends JsonToObjectConfiguration {

    private ForwarderConfiguration forwarderConfiguration;

    private final String SYSLOG = "syslog";

    public OutputConfiguration(JsonNode node) {
        setBadParams(new ArrayList<>());
        createConfiguration(node);
        badParamsAddKeys(forwarderConfiguration.badParams());
        checkStructure();
    }

    public ForwarderConfiguration getSyslogConfiguration() {
        return forwarderConfiguration;
    }


    public void setForwarderConfiguration(ForwarderConfiguration forwarderConfiguration) {
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
