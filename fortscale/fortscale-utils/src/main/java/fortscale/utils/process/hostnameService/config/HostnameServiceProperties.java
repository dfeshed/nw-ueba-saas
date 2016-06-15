package fortscale.utils.process.hostnameService.config;

import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HostnameServiceProperties {


    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("fortscale.process.hostname.service.cache.maxage",60); // hostname cache period in seconds
        properties.put("fortscale.process.hostname.service.disable",0); // hostname service disable functionality

        return properties;
    }
}
