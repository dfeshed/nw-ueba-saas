package presidio.output.forwarder.services;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.forwarder.AlertsForwarder;
import presidio.output.forwarder.Forwarder;
import presidio.output.forwarder.IndicatorsForwarder;
import presidio.output.forwarder.EntitiesForwarder;
import presidio.output.forwarder.shell.OutputForwarderApplication;


import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputForwardService {

    private static final Logger logger = Logger.getLogger(OutputForwarderApplication.class);

    EntitiesForwarder entitiesForwarder;
    AlertsForwarder alertsForwarder;
    IndicatorsForwarder indicatorsForwarder;
    MetricCollectingService metricCollectingService;

    public OutputForwardService(EntitiesForwarder entitiesForwarder, AlertsForwarder alertsForwarder, IndicatorsForwarder indicatorsForwarder, MetricCollectingService metricCollectingService) {
        this.entitiesForwarder = entitiesForwarder;
        this.alertsForwarder = alertsForwarder;
        this.indicatorsForwarder = indicatorsForwarder;
        this.metricCollectingService = metricCollectingService;
    }

    public int forward(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward data from %s to %s for %s", startDate, endDate, entityType));

        forwardEntities(startDate, endDate, entityType);

        List<String> alertIds = forwardAlerts(startDate, endDate, entityType);

        forwardIndicators(startDate, endDate, alertIds);

        logger.info("finish to forward data");

        return 0;
    }


    private void forwardEntities(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward entities from %s to %s for %s", startDate, endDate, entityType));

        Forwarder.ForwardedInstances forwardedInstances = entitiesForwarder.forwardEntities(startDate, endDate, entityType);

        reportMetric("entities", forwardedInstances.getForwardedCount(), startDate);
        logger.info("finish to forward {} entities", forwardedInstances.getForwardedCount());

    }

    private List<String> forwardAlerts(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward alerts from %s to %s for %s", startDate, endDate, entityType));

        Forwarder.ForwardedInstances forwardedInstances = alertsForwarder.forwardAlerts(startDate, endDate, entityType);

        reportMetric("alerts", forwardedInstances.getForwardedCount(), startDate);
        logger.info("finish to forward {} alerts", forwardedInstances.getForwardedCount());

        return forwardedInstances.getIds();

    }

    private void forwardIndicators(Instant startDate, Instant endDate, List<String> alertIds) {
        logger.info(String.format("about to forward indicators from %s to %s", startDate, endDate));

        Forwarder.ForwardedInstances forwardedInstances = indicatorsForwarder.forwardIndicators(alertIds);

        reportMetric("indicators", forwardedInstances.getForwardedCount(), startDate);
        logger.info("finish to forward {} indicators", forwardedInstances.getForwardedCount());

    }


    private void reportMetric(String type, int count, Instant startDate) {
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        String metricName = String.format("number_of_%s_forwarded", type);
        metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(metricName).
                setMetricValue(count).
                setMetricTags(tags).
                setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                setMetricLogicTime(startDate).
                build());
    }
}
