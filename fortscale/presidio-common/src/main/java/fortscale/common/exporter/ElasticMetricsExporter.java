package fortscale.common.exporter;


import fortscale.utils.logging.Logger;
import org.elasticsearch.client.Client;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.Map;
import java.util.Properties;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;


public class ElasticMetricsExporter extends MetricsExporter {

    private final Logger logger=Logger.getLogger(ElasticMetricsExporter.class);

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    private IndexQuery indexQuery;

    @Value("${cluster.name}")
    private String clusterName;
    @Value("${cluster.nodes}")
    private String clusterNodes;
    @Value("${client.transport.sniff}")
    private boolean clusterSniff;
    @Value("${client.transport.ignore_cluster_name}")
    private boolean clientTransportIgnoreClusterName;
    @Value("${client.transport.ping_timeout}")
    private String clientTransportPingTimeout;
    @Value("${client.transport.nodes_sampler_interval}")
    private String  clientTransportNodesSamplerInterval;



    public ElasticMetricsExporter(MetricsEndpoint metricsEndpoint) {
        super(metricsEndpoint);
        try {
            TransportClientFactoryBean transportClientFactoryBean=new TransportClientFactoryBean();
            transportClientFactoryBean.setClientIgnoreClusterName(clientTransportIgnoreClusterName);
            transportClientFactoryBean.setClientNodesSamplerInterval(clientTransportNodesSamplerInterval);
            transportClientFactoryBean.setClientPingTimeout(clientTransportPingTimeout);
            transportClientFactoryBean.setClientTransportSniff(clusterSniff);
            transportClientFactoryBean.setClusterName(clusterName);
            transportClientFactoryBean.setClusterNodes(clusterNodes);
            transportClientFactoryBean.afterPropertiesSet();
            Client client=transportClientFactoryBean.getObject();
            elasticsearchTemplate=new ElasticsearchTemplate(client);
            //elasticsearchTemplate=new ElasticsearchTemplate(nodeBuilder().local(true).node().client());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Scheduled(fixedRate = 5000)
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
