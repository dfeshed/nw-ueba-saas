package presidio.output.forwarder.config;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.output.forwarder.handlers.EventsHandler;
import presidio.output.forwarder.handlers.syslog.SyslogEndpoints;
import presidio.output.forwarder.handlers.syslog.SyslogEventsHandler;
import presidio.output.forwarder.services.SyslogService;

import java.util.Properties;

@Configuration
public class SyslogConfiguration {

//    private int syslogAlertsPort;
//    private int syslogUsersPort;

//
//    @Value("${syslog.users.host:}")
//    private String syslogUsersHostName;
//
//    @JsonSetter("${syslog.users.port:0}")
//    public void getSyslogUsersPort(String port) {
//        this.syslogUsersPort = Integer.valueOf(port);
//    }
//
//    @Value("${syslog.alerts.host:}")
//    private String syslogAlertsHostName;
//
//    @JsonSetter("${syslog.alerts.port:0}")
//    public void getSyslogAlertsPort(String port) {
//        this.syslogAlertsPort = Integer.valueOf(port);
//    }


    @Bean
    public SyslogService syslogService() {
        return new SyslogService();
    }

    @Bean
    public EventsHandler syslogForwarderEventsHandler() throws Exception {
        return new SyslogEventsHandler(syslogService(), syslogEndpoints());
    }

    @Autowired
    ConfigurationServerClientService configurationServerClientService;

    @Bean
    public SyslogEndpoints syslogEndpoints() throws Exception {
        SyslogEndpoints syslogEndpoints = new SyslogEndpoints();

        Properties prop = configurationServerClientService.readConfigurationAsProperties("application-presidio", "default");


        String userHost = prop.getProperty("outputForwarding.syslog.user.host");
        String userPort = prop.getProperty("outputForwarding.syslog.user.port");
        String alertHost = prop.getProperty("outputForwarding.syslog.alert.host");
        String alertPort = prop.getProperty("outputForwarding.syslog.alert.port");
        if (StringUtils.isNotEmpty(userHost)) {
            Integer port = 0;
            if (StringUtils.isNotEmpty(userPort)) {
                port = Integer.valueOf(userPort);
            }
            syslogEndpoints.addEndPoint("users", userHost, port);
        }
        if (StringUtils.isNotEmpty(alertHost)) {
            Integer port = 0;
            if (StringUtils.isNotEmpty(alertPort)) {
                port = Integer.valueOf(alertPort);
            }
            syslogEndpoints.addEndPoint("alerts", alertHost, port);
        }
        return syslogEndpoints;
    }


}
