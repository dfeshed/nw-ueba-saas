package presidio.monitoring.sdk.impl.factory;


import fortscale.utils.logging.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import presidio.monitoring.endPoint.PresidioSystemMetricsFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;
import presidio.monitoring.sdk.impl.spring.ExternalMonitoringConfiguration;

import java.io.Closeable;

public class PresidioExternalMonitoringServiceFactory implements Closeable {

    private static final Logger logger = Logger.getLogger(PresidioExternalMonitoringServiceFactory.class);

    private ConfigurableApplicationContext context;

    public PresidioExternalMonitoringService createPresidioExternalMonitoringService(String applicationName) throws Exception {
        context = new AnnotationConfigApplicationContext(ExternalMonitoringConfiguration.class);
        final PresidioExternalMonitoringService presidioExternalMonitoringServiceBean = context.getBean(PresidioExternalMonitoringService.class);
        if (presidioExternalMonitoringServiceBean == null) {
            final String errorMessage = "Failed to create PresidioExternalMonitoringService.";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        final PresidioSystemMetricsFactory presidioSystemMetricsFactory = context.getBean(PresidioSystemMetricsFactory.class);
        if (presidioSystemMetricsFactory == null) {
            final String errorMessage = "Failed to create PresidioSystemMetricsFactory. Couldn't get PresidioSystemMetricsFactory";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        presidioSystemMetricsFactory.setApplicationName(applicationName);
        return presidioExternalMonitoringServiceBean;
    }


    @Override
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
