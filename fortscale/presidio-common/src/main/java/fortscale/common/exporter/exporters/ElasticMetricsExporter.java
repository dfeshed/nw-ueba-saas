package fortscale.common.exporter.exporters;


import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;


import java.util.Map;


public class ElasticMetricsExporter extends MetricsExporter {

    private final Logger logger=Logger.getLogger(ElasticMetricsExporter.class);



    public ElasticMetricsExporter(MetricsEndpoint metricsEndpoint) {
        super(metricsEndpoint);

    }

    //@Scheduled(fixedRate = 5000)
    public void export() {
        logger.debug("Exporting");
    }


    private JSONObject createMetricObject(){
        logger.debug("Creating JSONObject of metrics to export");
        JSONObject metrics=new JSONObject();
        for (Map.Entry<String, String> entry : readyMetricsToExporter().entrySet()) {
            metrics.put(entry.getKey(),entry.getValue());
        }
        return metrics;
    }

    @Override
    public void close() throws Exception {
        export();
    }
}
