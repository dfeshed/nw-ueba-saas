package presidio.config.server.factory;
import fortscale.utils.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;


/**
 * This class allows its user to receive a Spring-created ConfigurationServerClientService (without knowing it was created by Spring)
 */
public class ConfigurationServerClientServiceFactory {

    private static final Logger logger = Logger.getLogger(ConfigurationServerClientServiceFactory.class);

    public ConfigurationServerClientService createConfigurationServerClientService() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigServerClientServiceConfiguration.class);
        final ConfigurationServerClientService configurationServerClientServiceBean = ctx.getBean(ConfigurationServerClientService.class);
        if (configurationServerClientServiceBean == null) {
            final String errorMessage = "Failed to create ConfigurationServerClientService.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        return configurationServerClientServiceBean;
    }
}
