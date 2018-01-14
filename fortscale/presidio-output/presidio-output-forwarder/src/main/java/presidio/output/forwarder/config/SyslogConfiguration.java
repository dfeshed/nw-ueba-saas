package presidio.output.forwarder.config;


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

    @Value("${syslog.users.host:}")
    private String syslogUsersHostName;

    @Value("${syslog.users.port:0}")
    private int syslogUsersPort;

    @Value("${syslog.alerts.host:}")
    private String syslogAlertsHostName;

    @Value("${syslog.alerts.port:0}")
    private int syslogAlertsPort;


    @Bean
    public SyslogService syslogService() {
        return new SyslogService();
    }

    @Bean
    public EventsHandler syslogForwarderEventsHandler(){
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
        return  syslogEndpoints;
    }


}
