package fortscale.common.exporter;

import fortscale.utils.logging.Logger;
import org.elasticsearch.client.Client;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.Map;
import java.util.Properties;


public class ElasticMetricsExporter extends MetricsExporter {

    private final Logger logger=Logger.getLogger(ElasticMetricsExporter.class);
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    private IndexQuery indexQuery;
    private Properties properties;
    private MetricsEndpoint metricsEndpoint;

    public ElasticMetricsExporter(MetricsEndpoint metricsEndpoint) {
        super();
        try {
            this.metricsEndpoint=metricsEndpoint;
            TransportClientFactoryBean transportClientFactoryBean=new TransportClientFactoryBean();
            transportClientFactoryBean.setProperties(properties);
            transportClientFactoryBean.afterPropertiesSet();
            Client cliet=transportClientFactoryBean.getObject();
            elasticsearchTemplate=new ElasticsearchTemplate(cliet);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Scheduled(fixedRate = 5000)
    public void export() {
        createQuery();
        logger.debug("Exporting");
        elasticsearchTemplate.index(indexQuery);
    }

    private void createQuery(){
        this.indexQuery=new IndexQuery();
        this.indexQuery.setId("");
        this.indexQuery.setObject(createMetricObject());
    }

    private JSONObject createMetricObject(){
        JSONObject metrics=new JSONObject();
        try {
            logger.debug("Exporting");
            String metric;
            String value;
            Map<String, Object> map = metricsEndpoint.invoke();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                metric = entry.getKey();
                value = entry.getValue().toString();
                if (!fixedMetrics.contains(metric)) {
                    if (!customMetrics.containsKey(metric))
                        customMetrics.put(metric, value);
                    else {
                        if (!customMetrics.get(metric).equals(value))
                            customMetrics.replace(metric, value);
                        else {
                            break;
                        }
                    }
                }
                metrics.append(metric,value);
            }
        }
        catch (Exception ex){
            logger.error("NOT GOOD");
        }
        return  metrics;
    }

    @Override
    public void close() throws Exception {
        export();
    }
}
