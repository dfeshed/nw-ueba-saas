package com.rsa.asoc.sa.ui.common.config;

import com.rsa.netwitness.carlos.common.http.ComponentsClientHttpParamsFactoryBean;
import com.rsa.netwitness.carlos.config.store.FileBackingStore;
import com.rsa.netwitness.carlos.transport.MessageEndpointFactory;
import com.rsa.netwitness.carlos.transport.amqp.AmqpMessageTransportFactory;
import com.rsa.netwitness.carlos.transport.amqp.RmqBrokerConfiguration;
import com.rsa.netwitness.carlos.transport.impl.DefaultMessageEndpointFactory;
import com.rsa.netwitness.carlos.transport.jms.JmsMessageTransportFactory;
import com.rsa.netwitness.carlos.transport.nextgen.NextGenClientFactory;
import com.rsa.netwitness.carlos.transport.nw.NwMessageTransportFactory;
import com.rsa.netwitness.carlos.transport.spi.MessageTransportFactory;
import com.rsa.netwitness.carlos.transport.ssl.KeyStoreFactory;
import com.rsa.netwitness.carlos.transport.ssl.SSLContextConfiguration;
import com.rsa.netwitness.carlos.transport.ssl.SSLContextConfigurationMXBean;
import com.rsa.netwitness.carlos.transport.ssl.SSLContextFactory;
import com.rsa.netwitness.carlos.transport.ssl.impl.DefaultKeyStoreFactory;
import com.rsa.netwitness.carlos.transport.ssl.impl.DefaultTrustStoreFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.UUID;

/**
 * Configuration for CARLOS Transports.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Configuration
public class CarlosTransportConfig {

    @Bean
    public CarlosTransportSettings transportSettings() {
        return new CarlosTransportSettings();
    }

    @Bean
    public FileBackingStore fileBackingStore() {
        return new FileBackingStore();
    }

    @Bean
    public SSLContextConfigurationMXBean sslContextConfigurationMxBean() {
        return new SSLContextConfiguration();
    }

    @Bean
    public KeyStoreFactory keyStoreFactory() {
        DefaultKeyStoreFactory factory = new DefaultKeyStoreFactory();
        factory.setSslContextConfiguration(sslContextConfigurationMxBean());
        return factory;
    }

    @Bean
    public KeyStoreFactory trustStoreFactory() {
        DefaultTrustStoreFactory factory = new DefaultTrustStoreFactory();
        factory.setSslContextConfiguration(sslContextConfigurationMxBean());
        return factory;
    }

    @Bean
    public SSLContext sslContextFactory() throws Exception {
        SSLContextFactory factory = new SSLContextFactory();
        factory.setKeyStoreFactory(keyStoreFactory());
        factory.setTrustStoreFactory(trustStoreFactory());
        factory.setSslContextConfiguration(sslContextConfigurationMxBean());
        return factory.getObject();
    }

    /**
     * Configure the JMS/ActiveMQ transport.
     */
    @Bean
    @ConditionalOnProperty(prefix = "carlos.transport.jms", name = "enabled", matchIfMissing = true)
    public MessageTransportFactory jmsMessageTransportFactory() throws Exception {
        JmsMessageTransportFactory factory = new JmsMessageTransportFactory();
        factory.setBrokerService(jmsBroker());
        factory.setConnectionFactory(jmsPooledConnectionFactory());
        return factory;
    }

    private BrokerService jmsBroker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(UUID.randomUUID().toString());
        broker.setSupportFailOver(true);
        broker.setEnableStatistics(false);
        broker.setUseJmx(false);
        broker.setPersistent(false);
        broker.setUseShutdownHook(true);
        broker.setNetworkConnectorStartAsync(true);

        SslContext jmsSslContext = new SslContext();
        jmsSslContext.setSSLContext(sslContextFactory());
        broker.setSslContext(jmsSslContext);

        ManagementContext context = new ManagementContext();
        context.setCreateConnector(false);
        broker.setManagementContext(context);

        MemoryUsage memoryUsage = new MemoryUsage();
        memoryUsage.setLimit(transportSettings().getJms().getMemoryLimit());
        SystemUsage usage = new SystemUsage();
        usage.setMemoryUsage(memoryUsage);
        usage.setSendFailIfNoSpaceAfterTimeout(transportSettings().getJms().getSendFailIfNoSpaceAfterTimeout());

        broker.start();
        return broker;
    }

    private PooledConnectionFactory jmsPooledConnectionFactory() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectionFactory(jmsConnectionFactory());
        return factory;
    }

    private ActiveMQConnectionFactory jmsConnectionFactory() {
        ActiveMQPrefetchPolicy policy = new ActiveMQPrefetchPolicy();
        policy.setQueuePrefetch(transportSettings().getJms().getQueuePrefetch());

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setPrefetchPolicy(policy);
        return factory;
    }

    /**
     * Configure the AMQP/RabbitMQ transport.
     */
    @Bean
    @ConditionalOnProperty(prefix = "carlos.transport.amqp", name = "enabled", matchIfMissing = true)
    public AmqpMessageTransportFactory amqpMessageTransportFactory() throws Exception {
        AmqpMessageTransportFactory factory = new AmqpMessageTransportFactory();
        factory.setConnectionConfiguration(amqpBrokerConfiguration());
        return factory;
    }

    private RmqBrokerConfiguration amqpBrokerConfiguration() throws Exception {
        return new RmqBrokerConfiguration();
    }

    /**
     * Configure the NetWitness transport.
     */
    @Bean
    @ConditionalOnProperty(prefix = "carlos.transport.nw", name = "enabled", matchIfMissing = true)
    public NwMessageTransportFactory nwMessageTransportFactory() throws Exception {
        NwMessageTransportFactory factory = new NwMessageTransportFactory(nextGenClientFactory());
        factory.setSslContext(sslContextFactory());
        return factory;
    }

    @SuppressWarnings("deprecation")
    private NextGenClientFactory nextGenClientFactory() throws Exception {
        // The ConnectionManager settings are only used for REST connections, so they
        // can be ignored and not externalized.
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(2048);
        connectionManager.setDefaultMaxPerRoute(100);

        ComponentsClientHttpParamsFactoryBean httpParamsFactoryBean = new ComponentsClientHttpParamsFactoryBean();
        httpParamsFactoryBean.setConnectionTimeout(transportSettings().getNw().getConnectionTimeout());
        httpParamsFactoryBean.setSoTimeout(transportSettings().getNw().getSocketTimeout());
        httpParamsFactoryBean.setStaleCheckingEnabled(true);

        NextGenClientFactory factory = new NextGenClientFactory(connectionManager,
                (HttpParams)httpParamsFactoryBean.getObject());
        factory.setSslContext(sslContextFactory());
        return factory;
    }

    /**
     * Configure the {@link MessageEndpointFactory} if there are {@link MessageTransportFactory}s defined.
     */
    @Configuration
    protected static class TransportFactoryConfiguration {
        @Autowired(required = false)
        private List<MessageTransportFactory> transportFactories;

        @Bean
        public MessageEndpointFactory messageEndpointFactory() throws Exception {
            DefaultMessageEndpointFactory messageEndpointFactory = new DefaultMessageEndpointFactory();
            messageEndpointFactory.setEndpointFactories(transportFactories);
            return messageEndpointFactory;
        }
    }
}