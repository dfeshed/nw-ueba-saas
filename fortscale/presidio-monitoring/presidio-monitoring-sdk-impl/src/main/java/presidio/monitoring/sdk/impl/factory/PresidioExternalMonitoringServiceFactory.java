package presidio.monitoring.sdk.impl.factory;


import fortscale.utils.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringConfiguration;

public class PresidioExternalMonitoringServiceFactory {

    private static final Logger logger = Logger.getLogger(PresidioExternalMonitoringServiceFactory.class);

    public PresidioExternalMonitoringService createPresidioExternalMonitoringService() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ExternalMonitoringConfiguration.class);
        final PresidioExternalMonitoringService presidioExternalMonitoringServiceBean = ctx.getBean(PresidioExternalMonitoringService.class);
        if (presidioExternalMonitoringServiceBean == null) {
            final String errorMessage = "Failed to create PresidioExternalMonitoringService.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        return presidioExternalMonitoringServiceBean;

    }
}
