package presidio.manager.api.records;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;
import presidio.manager.api.configuration.SyslogSenderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SyslogMessageSenderConfiguration extends createConfigurationAndStructureValidiation implements SyslogSenderConfiguration {

    private String host;
    private int port = -1;

    private final String HOST = "host";
    private final String PORT = "port";

    public SyslogMessageSenderConfiguration(JsonNode node) {
        setBadParams(new ArrayList<>());
        createConfiguration(node);
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
        setStructureValid(isValid() && !StringUtils.isEmpty(host) && port != -1);
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
}
