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

        Forwarder.ForwardedInstances forwarderEntity = entitiesForwarder.forwardEntities(startDate, endDate, entityType);

        reportMetric("entities", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward {} entities", forwarderEntity.getForwardedCount());

    }

    private List<String> forwardAlerts(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward alerts from %s to %s for %s", startDate, endDate, entityType));

        Forwarder.ForwardedInstances forwarderEntity = alertsForwarder.forwardAlerts(startDate, endDate, entityType);

        reportMetric("alerts", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward {} alerts", forwarderEntity.getForwardedCount());

        return forwarderEntity.getIds();

    }

    private void forwardIndicators(Instant startDate, Instant endDate, List<String> alertIds) {
        String alertIdsStr = "(" + StringUtils.join(alertIds, ") (") + ")";
        logger.info(String.format("about to forward indicators from %s to %s for the following alert id's: %s", startDate, endDate, alertIdsStr));

        Forwarder.ForwardedInstances forwarderEntity = indicatorsForwarder.forwardIndicators(alertIds);

        reportMetric("indicators", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward {} indicators", forwarderEntity.getForwardedCount());

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
