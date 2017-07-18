package fortscale.common.exporter;


import fortscale.utils.logging.Logger;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchTemplateProducer {

    private final Logger logger =Logger.getLogger(ElasticSearchTemplateProducer.class);

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

    public ElasticSearchTemplateProducer(){}

    public ElasticsearchTemplate produceElasticsearchTemplate(){
        logger.debug("Creating ElasticsearchTemplate with params {} , {} , {} , {} , {}, {}",
                clusterName,clusterNodes,clusterSniff,clientTransportIgnoreClusterName,clientTransportPingTimeout
        ,clientTransportNodesSamplerInterval);
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
            return new ElasticsearchTemplate(client);
            //elasticsearchTemplate=new ElasticsearchTemplate(nodeBuilder().local(true).node().client());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
