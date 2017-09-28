package presidio.security.manager.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;
import presidio.security.manager.service.ConfigurationSecurityService;

@Configuration
@Import(ConfigServerClientServiceConfiguration.class)
public class SecurityManagerConfiguration {

    @Autowired
    @Qualifier("configurationServerClientService")
    private ConfigurationServerClientService configurationServerClientService;

    @Autowired
    freemarker.template.Configuration freemarkerConfiguration;


    @Bean(name = "configurationSecurityService")
    public ConfigurationSecurityService configurationSecurityService() {
        return new ConfigurationSecurityService(configurationServerClientService, freemarkerConfiguration);
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();
        fmConfigFactoryBean.setTemplateLoaderPath("templates/");
        return fmConfigFactoryBean;
    }

}
