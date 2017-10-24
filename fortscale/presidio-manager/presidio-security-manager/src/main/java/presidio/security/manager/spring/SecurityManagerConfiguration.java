package presidio.security.manager.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    public static final String FILE_RESOURCE = "file:/";
    @Value("${manager.security.securityConfPath:/etc/httpd/conf/httpd.conf}")
    private String securityConfPath;

    @Value("${manager.security.krb5ConfPath:/etc/krb5.conf}")
    private String krb5ConfPath;

    @Value("${manager.security.shouldReloadHttpd:true}")
    private boolean shouldReloadHttpd;

    @Autowired
    @Qualifier("configurationServerClientService")
    private ConfigurationServerClientService configurationServerClientService;

    @Autowired
    freemarker.template.Configuration freemarkerConfiguration;


    @Bean(name = "configurationSecurityService")
    public ConfigurationSecurityService configurationSecurityService() {
        return new ConfigurationSecurityService(configurationServerClientService, freemarkerConfiguration,
                securityConfPath, krb5ConfPath, shouldReloadHttpd);
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();
        String resourceName = ConfigurationSecurityService.TEMPLATE_DIRECTORY;
        String templateDir = FILE_RESOURCE + getClass().getResource(resourceName).getPath();
        fmConfigFactoryBean.setPreferFileSystemAccess(true);
        fmConfigFactoryBean.setTemplateLoaderPath(templateDir);
        return fmConfigFactoryBean;
    }

}
