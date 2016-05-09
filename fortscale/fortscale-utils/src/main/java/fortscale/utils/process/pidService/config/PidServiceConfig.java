package fortscale.utils.process.pidService.config;

import fortscale.utils.spring.PropertySourceConfigurer;
import fortscale.utils.process.pidService.PidService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by baraks on 5/2/2016.
 */
@Configuration
public class PidServiceConfig {
    @Value("${fortscale.pid.folder.path}")
    private String pidDir;
    @Value("${group.pid.folder.name}")
    private String groupPidFolderName;
    @Value("${daemon.pid.file.name}")
    private String pidfFileName;

    @Bean(destroyMethod = "shutdown")
    public PidService pidService()
    {
        return new PidService(pidDir,groupPidFolderName,pidfFileName);
    }

    @Bean
    private static PropertySourceConfigurer pidServicePropertyConfigurer() {
        Properties properties = PidServiceProperties.getProperties();
        PropertySourceConfigurer configurer = new PropertySourceConfigurer(PidServiceConfig.class, properties);
        return configurer;
    }

}
