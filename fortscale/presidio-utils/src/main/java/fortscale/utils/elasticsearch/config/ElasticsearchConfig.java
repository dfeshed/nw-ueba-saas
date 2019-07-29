package fortscale.utils.elasticsearch.config;


import fortscale.utils.elasticsearch.PresidioElasticsearchMappingContext;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.PresidioResultMapper;
import fortscale.utils.elasticsearch.services.TemplateAnnotationExtractor;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.join.ParentJoinPlugin;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;

    @Bean
    public Client client() throws Exception {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        Settings esSettings = Settings.builder().put("cluster.name", EsClusterName).build();
        return new SpringDataTransportClient(esSettings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
    }

    @Bean
    public MappingElasticsearchConverter mappingElasticsearchConverter() {
        return new MappingElasticsearchConverter(new PresidioElasticsearchMappingContext());
    }

    @Bean
    public ResultsMapper resultsMapper() {
        return new PresidioResultMapper(mappingElasticsearchConverter().getMappingContext());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new PresidioElasticsearchTemplate(client(), new TemplateAnnotationExtractor(), resultsMapper());
    }


    /**
     * Pretty exact copy of {@link PreBuiltTransportClient} except that we're inspecting the classpath for Netty
     * dependencies to only include the ones available. {@link PreBuiltTransportClient} expects both Netty 3 and Netty 4
     * to be present.
     *
     * @author Oliver Gierke
     * see https://github.com/elastic/elasticsearch/issues/31240
     */
    @SuppressWarnings("unchecked")
    private static class SpringDataTransportClient extends TransportClient {

        /**
         * Netty wants to do some unwelcome things like use unsafe and replace a private field, or use a poorly considered
         * buffer recycler. This method disables these things by default, but can be overridden by setting the corresponding
         * system properties.
         */
        private static void initializeNetty() {
            /*
             * We disable three pieces of Netty functionality here:
             *  - we disable Netty from being unsafe
             *  - we disable Netty from replacing the selector key set
             *  - we disable Netty from using the recycler
             *
             * While permissions are needed to read and set these, the permissions needed here are innocuous and thus should simply be granted
             * rather than us handling a security exception here.
             */
            setSystemPropertyIfUnset("io.netty.noUnsafe", Boolean.toString(true));
            setSystemPropertyIfUnset("io.netty.noKeySetOptimization", Boolean.toString(true));
            setSystemPropertyIfUnset("io.netty.recycler.maxCapacityPerThread", Integer.toString(0));
        }

        @SuppressForbidden(reason = "set system properties to configure Netty")
        private static void setSystemPropertyIfUnset(final String key, final String value) {
            final String currentValue = System.getProperty(key);
            if (currentValue == null) {
                System.setProperty(key, value);
            }
        }

        private static final List<String> OPTIONAL_DEPENDENCIES = Arrays.asList( //
                "org.elasticsearch.transport.Netty3Plugin", //
                "org.elasticsearch.transport.Netty4Plugin");

        private static final Collection<Class<? extends Plugin>> PRE_INSTALLED_PLUGINS;

        static {

            initializeNetty();

            List<Class<? extends Plugin>> plugins = new ArrayList<>();
            boolean found = false;

            for (String dependency : OPTIONAL_DEPENDENCIES) {
                try {
                    plugins.add((Class<? extends Plugin>) ClassUtils.forName(dependency,
                            SpringDataTransportClient.class.getClassLoader()));
                    found = true;
                } catch (ClassNotFoundException | LinkageError e) {}
            }

            Assert.state(found, "Neither Netty 3 or Netty 4 plugin found on the classpath. One of them is required to run the transport client!");

            plugins.add(ReindexPlugin.class);
            plugins.add(PercolatorPlugin.class);
            plugins.add(MustachePlugin.class);
            plugins.add(ParentJoinPlugin.class);

            PRE_INSTALLED_PLUGINS = Collections.unmodifiableList(plugins);
        }

        public SpringDataTransportClient(Settings settings) {
            super(settings, PRE_INSTALLED_PLUGINS);
        }

        @Override
        public void close() {
            super.close();
            if (!NetworkModule.TRANSPORT_TYPE_SETTING.exists(settings)
                    || NetworkModule.TRANSPORT_TYPE_SETTING.get(settings).equals(Netty4Plugin.NETTY_TRANSPORT_NAME)) {
                try {
                    GlobalEventExecutor.INSTANCE.awaitInactivity(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                try {
                    ThreadDeathWatcher.awaitInactivity(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


}