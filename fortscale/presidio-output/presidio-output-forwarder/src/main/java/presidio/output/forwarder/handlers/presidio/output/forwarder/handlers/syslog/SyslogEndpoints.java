package presidio.output.forwarder.handlers.presidio.output.forwarder.handlers.syslog;

import java.util.HashMap;
import java.util.Map;


public class SyslogEndpoints {

    private Map<String, SyslogEndpoint> endpoints;


    public SyslogEndpoints() {
        endpoints = new HashMap<String, SyslogEndpoint>();
    }

    public Map<String, SyslogEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, SyslogEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public void addEndPoint(String type, String host, int port) {
        endpoints.put(type, new SyslogEndpoint(host, port));
    }

    public SyslogEndpoint getEndPoint(String type) {
        return endpoints.get(type);
    }

    class SyslogEndpoint {
        String host;
        int port;

        public SyslogEndpoint(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
