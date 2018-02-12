package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import presidio.manager.api.configuration.ConfigurationValidatable;

import java.util.List;

public class SyslogConfigurationCreation extends ConfigurationCreation implements ConfigurationValidatable {

    private String host;
    private int port = 0;

    private final String HOST = "host";
    private final String PORT = "port";

    public SyslogConfigurationCreation() {
    }

    public SyslogConfigurationCreation(JsonNode node) {
        createConfiguration(node);
        if (StringUtils.isEmpty(host)) {
            missingParamsAddKeys(HOST);
        }
        if (port == 0) {
            missingParamsAddKeys(PORT);
        }
        checkStructure();
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }


    @Override
    void checkStructure() {
        setStructureValid(isValid() && !StringUtils.isEmpty(host) && port != 0);
    }

    @Override
    public void setKeyValue(String key, JsonNode value) {
        switch (key) {
            case HOST:
                setHost(value.asText());
                break;
            case PORT:
                setPort(Integer.parseInt(value.asText()));
                break;
            default:
                badParamsAddKey(key);
        }
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
}
