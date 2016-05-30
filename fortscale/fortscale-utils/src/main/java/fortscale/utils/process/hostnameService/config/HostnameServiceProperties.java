package fortscale.utils.process.hostnameService.config;

import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HostnameServiceProperties {


    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("fortscale.hostname.cacheperiod",60); // hostname cache period in seconds
        return properties;
    }
}
