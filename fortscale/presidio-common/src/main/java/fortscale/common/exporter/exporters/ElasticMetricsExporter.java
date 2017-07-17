package fortscale.common.exporter.exporters;


import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;


import java.util.Map;


public class ElasticMetricsExporter extends MetricsExporter {

    private final Logger logger=Logger.getLogger(ElasticMetricsExporter.class);

    private ElasticsearchTemplate elasticsearchTemplate;
    private IndexQuery indexQuery;


    public ElasticMetricsExporter(MetricsEndpoint metricsEndpoint,ElasticsearchTemplate elasticsearchTemplate) {
        super(metricsEndpoint);
        this.elasticsearchTemplate=elasticsearchTemplate;
    }

    //@Scheduled(fixedRate = 5000)
    public void export() {
        logger.debug("Exporting");
        createQuery();
        elasticsearchTemplate.index(indexQuery);
    }

    private void createQuery(){
        this.indexQuery=new IndexQuery();
        this.indexQuery.setId("");
        this.indexQuery.setObject(createMetricObject());
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
