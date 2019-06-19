package presidio.output.forwarder.services;

import fortscale.utils.logging.Logger;
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
        logger.info(String.format("about to forward data from %s to %s", startDate, endDate));

        forwardEntities(startDate, endDate, entityType);

        List<String> alertIds = forwardAlerts(startDate, endDate, entityType);

        forwardIndicators(startDate, endDate, alertIds);

        logger.info("finish to forward data");

        return 0;
    }


    public void forwardEntities(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward entities from %s to %s", startDate, endDate));

        Forwarder.ForwardedEntity forwarderEntity = entitiesForwarder.forwardEntities(startDate, endDate, entityType);

        reportMetric("entities", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward {} entities", forwarderEntity.getForwardedCount());

    }

    public List<String> forwardAlerts(Instant startDate, Instant endDate, String entityType) {
        logger.info(String.format("about to forward alerts from %s to %s", startDate, endDate));

        Forwarder.ForwardedEntity forwarderEntity = alertsForwarder.forwardAlerts(startDate, endDate, entityType);

        reportMetric("alerts", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward {} alerts", forwarderEntity.getForwardedCount());

        return forwarderEntity.getIds();

    }

    public void forwardIndicators(Instant startDate, Instant endDate, List<String> alertIds) {
        logger.info(String.format("about to forward indicators from %s to %s", startDate, endDate));

        Forwarder.ForwardedEntity forwarderEntity = indicatorsForwarder.forwardIndicators(alertIds);

        reportMetric("indicators", forwarderEntity.getForwardedCount(), startDate);
        logger.info("finish to forward indicators", forwarderEntity.getForwardedCount());

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
