package fortscale.utils.elasticsearch.config;

import fortscale.utils.logging.Logger;
import org.junit.Assert;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Manage embedded elasticsearch for test purpose.
 * EmbeddedElasticsearchInitialiser bean must be initialized by spring before the elasticsearch client is created
 *
 * Created by efratn on 17/10/2017.
 */
public class EmbeddedElasticsearchInitialiser {

    public static final String EL_DOWNLOAD_URL = "https://libhq-ro.rsa.lab.emc.com/SA/tools/elastic/elasticsearch-5.0.0.zip";
    private final Logger logger = Logger.getLogger(EmbeddedElasticsearchInitialiser.class);

    public static String EL_TEST_VERSION = "5.0.0";
    public static String EL_TEST_PORT = "9350";
    public static String EL_TEST_CLUSTER = "fortscalse_test";

    private EmbeddedElastic embeddedElastic = null;

    public void start() throws IOException, InterruptedException {
        if(embeddedElastic != null) {
            logger.debug("embedded elasticsearch already started, skipping startup");
            return;
        }
        logger.debug("starting embedded elasticsearch");

        try {
            embeddedElastic = EmbeddedElastic.builder()
                    .withElasticVersion(EL_TEST_VERSION)
                    .withStartTimeout(2, TimeUnit.MINUTES)
                    .withSetting(PopularProperties.TRANSPORT_TCP_PORT, EL_TEST_PORT)
                    .withSetting(PopularProperties.CLUSTER_NAME, EL_TEST_CLUSTER)
                    .withDownloadUrl(new URL(EL_DOWNLOAD_URL))
                    .withSetting("node.max_local_storage_nodes", 3)
                    .withCleanInstallationDirectoryOnStop(true)
                    .build()
                    .start();
        } catch (Exception e) {
            embeddedElastic = null;
            logger.error("Failed to start elasticsearch",e);
            throw e;
        }
    }

    public void stop() {
        logger.debug("stopping embedded elasticsearch");
        embeddedElastic.stop();

    }

}
