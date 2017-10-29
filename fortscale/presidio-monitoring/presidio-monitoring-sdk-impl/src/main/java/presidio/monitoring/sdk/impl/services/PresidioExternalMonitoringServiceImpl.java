package presidio.monitoring.sdk.impl.services;


import presidio.monitoring.aspect.services.MetricCollectingService;
import presidio.monitoring.elastic.records.PresidioMetric;
import presidio.monitoring.factory.PresidioMetricFactory;
import presidio.monitoring.sdk.api.services.PresidioExternalMonitoringService;

public class PresidioExternalMonitoringServiceImpl implements PresidioExternalMonitoringService {

    private MetricCollectingService metricCollectingService;
    private PresidioMetricFactory presidioMetricFactory;

    private final String FILTERED_EVENT_METRIC = "number.of.filtered.event";
    private final String NUMBER_OF_PROCESSED_EVENTS_METRIC = "number.of.processed.events";
    private final String TYPE_LONG = "long";


    public PresidioExternalMonitoringServiceImpl() {
    }

    public PresidioExternalMonitoringServiceImpl(MetricCollectingService metricCollectingService, PresidioMetricFactory presidioMetricFactory) {
        this.presidioMetricFactory = presidioMetricFactory;
        this.metricCollectingService = metricCollectingService;
    }

    public PresidioMetric creatingPresidioMetric(String metricName, Number metricValue, String unit) {
        return presidioMetricFactory.creatingPresidioMetric(metricName, metricValue, unit);
    }


    @Override
    public void reportCustomMetric(PresidioMetric presidioMetric) {
        metricCollectingService.addMetric(presidioMetric);
    }

    @Override
    public void reportCustomMetricReportOnce(PresidioMetric presidioMetric) {
        metricCollectingService.addMetricReportOnce(presidioMetric);
    }


}
