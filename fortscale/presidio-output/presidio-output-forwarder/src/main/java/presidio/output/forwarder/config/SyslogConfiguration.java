package presidio.output.forwarder.config;


import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.forwarder.handlers.EventsHandler;
import presidio.output.forwarder.handlers.syslog.SyslogEndpoints;
import presidio.output.forwarder.handlers.syslog.SyslogEventsHandler;
import presidio.output.forwarder.services.SyslogService;

@Configuration
public class SyslogConfiguration {

    private int syslogAlertsPort;
    private int syslogUsersPort;


    @Value("${syslog.users.host:}")
    private String syslogUsersHostName;

    @JsonSetter("${syslog.users.port:0}")
    public void getSyslogUsersPort(String port) {
        this.syslogUsersPort = Integer.valueOf(port);
    }

    @Value("${syslog.alerts.host:}")
    private String syslogAlertsHostName;

    @JsonSetter("${syslog.alerts.port:0}")
    public void getSyslogAlertsPort(String port) {
        this.syslogAlertsPort = Integer.valueOf(port);
    }


    @Bean
    public SyslogService syslogService() {
        return new SyslogService();
    }

    @Bean
    public EventsHandler syslogForwarderEventsHandler() {
        return new SyslogEventsHandler(syslogService(), syslogEndpoints());
    }

    @Bean
    public SyslogEndpoints syslogEndpoints() {
        SyslogEndpoints syslogEndpoints = new SyslogEndpoints();
        if (StringUtils.isNotEmpty(syslogUsersHostName)) {
            syslogEndpoints.addEndPoint("users", syslogUsersHostName, syslogUsersPort);
        }
        if (StringUtils.isNotEmpty(syslogAlertsHostName)) {
            syslogEndpoints.addEndPoint("alerts", syslogAlertsHostName, syslogAlertsPort);
        }
        return syslogEndpoints;
    }


}
