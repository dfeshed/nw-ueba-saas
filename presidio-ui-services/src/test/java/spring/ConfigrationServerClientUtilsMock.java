package spring;

import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

public class ConfigrationServerClientUtilsMock extends ConfigrationServerClientUtils{

    Properties properties;
    public ConfigrationServerClientUtilsMock(Properties properties){
        this(null,null,null,null);
        this.properties = properties;
    }
    public ConfigrationServerClientUtilsMock(RestTemplate restTemplate, String serverUrl, String serverUserName, String serverUserNamePassword) {
        super(restTemplate, serverUrl, serverUserName, serverUserNamePassword);
    }


    @Override
    public Properties readConfigurationAsProperties(String moduleName, String profile) throws Exception {
       return properties;
    }

}
