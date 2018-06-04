package presidio.output.forwarder.services;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.forwarder.AlertsForwarder;
import presidio.output.forwarder.IndicatorsForwarder;
import presidio.output.forwarder.UsersForwarder;
import presidio.output.forwarder.shell.OutputForwarderApplication;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OutputForwardService {

    private static final Logger logger = Logger.getLogger(OutputForwarderApplication.class);

    UsersForwarder usersForwarder;
    AlertsForwarder alertsForwarder;
    IndicatorsForwarder indicatorsForwarder;
    MetricCollectingService metricCollectingService;

    public OutputForwardService(UsersForwarder usersForwarder, AlertsForwarder alertsForwarder, IndicatorsForwarder indicatorsForwarder, MetricCollectingService metricCollectingService) {
        this.usersForwarder = usersForwarder;
        this.alertsForwarder = alertsForwarder;
        this.indicatorsForwarder = indicatorsForwarder;
        this.metricCollectingService = metricCollectingService;
    }

    public int forward(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward data from %s to %s", startDate, endDate));

        forwardUsers(startDate, endDate);

        forwardAlerts(startDate, endDate);

        forwardIndicators(startDate, endDate);

        logger.info("finish to forward data");

        return 0;
    }


    public void forwardUsers(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward users from %s to %s", startDate, endDate));

        int count = usersForwarder.forward(startDate, endDate);

        reportMetric("users", count, startDate);
        logger.info("finish to forward {} users", count);

    }

    public void forwardAlerts(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward alerts from %s to %s", startDate, endDate));

        int count = alertsForwarder.forward(startDate, endDate);

        reportMetric("alerts", count, startDate);
        logger.info("finish to forward {} alerts", count);

    }

    public void forwardIndicators(Instant startDate, Instant endDate) {
        logger.info(String.format("about to forward indicators from %s to %s", startDate, endDate));

        int count = indicatorsForwarder.forward(startDate, endDate);

        reportMetric("indicators", count, startDate);
        logger.info("finish to forward indicators", count);

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
