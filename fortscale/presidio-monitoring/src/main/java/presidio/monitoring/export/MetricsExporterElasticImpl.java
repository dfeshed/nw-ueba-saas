package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import presidio.monitoring.elastic.records.PresidioMetric;
import presidio.monitoring.elastic.services.MetricExportService;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger=Logger.getLogger(MetricsExporterElasticImpl.class);

    private MetricExportService metricExportService;


    public MetricsExporterElasticImpl(MetricsEndpoint metricsEndpoint, String applicationName,MetricExportService metricExportService) {
        super(metricsEndpoint,applicationName);
        this.metricExportService=metricExportService;
    }


    @Scheduled(fixedRate = 5000)
    public void export() {
        logger.debug("Exporting metrics to elastic");
        metricExportService.save(filterRepitMetrics());
    }


    @Override
    public void close() throws Exception {
        export();
    }
}
