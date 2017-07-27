package presidio.monitoring.export;


import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.Map;


public class MetricsExporterElasticImpl extends MetricsExporter {

    private final Logger logger=Logger.getLogger(MetricsExporterElasticImpl.class);



    public MetricsExporterElasticImpl(MetricsEndpoint metricsEndpoint, String applicationName) {
        super(metricsEndpoint,applicationName);
    }


    @Scheduled(fixedRate = 5000)
    public void export() {
        logger.info("{}",createMetricObject());
    }


    private JSONObject createMetricObject(){
        logger.debug("Creating JSONObject of metrics to export");
        JSONObject metrics=new JSONObject();
        for (Map.Entry<String, Object> entry : filterRepitMetrics().entrySet()) {
            metrics.put(entry.getKey(),entry.getValue());
        }
        return metrics;
    }

    @Override
    public void close() throws Exception {
        export();
    }
}
