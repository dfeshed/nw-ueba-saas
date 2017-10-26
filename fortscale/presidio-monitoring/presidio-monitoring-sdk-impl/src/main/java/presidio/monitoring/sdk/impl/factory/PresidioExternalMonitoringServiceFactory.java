package presidio.monitoring.sdk.impl.factory;


import fortscale.utils.logging.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import presidio.monitoring.export.MetricsExporter;
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
        final MetricsExporter metricsExporterBean = context.getBean(MetricsExporter.class);
        if (metricsExporterBean == null) {
            final String errorMessage = "Failed to create PresidioExternalMonitoringService. Couldn't get MetricsExporter";
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
        metricsExporterBean.setApplicationName(applicationName);
        return presidioExternalMonitoringServiceBean;
    }


    @Override
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}
