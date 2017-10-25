package fortscale.utils.elasticsearch.config;

import org.junit.Assert;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Created by efratn on 17/10/2017.
 */
public class EmbeddedElasticsearchInitialiser {

    public static String EL_TEST_VERSION = "5.0.0";
    public static String EL_TEST_PORT = "9350";
    public static String EL_TEST_CLUSTER = "fortscalse_test";

    private EmbeddedElastic embeddedElastic;

    @PostConstruct
    public void setupEmbeddedElasticsearch() {

        try {
            embeddedElastic = EmbeddedElastic.builder()
                    .withElasticVersion(EL_TEST_VERSION)
                    .withStartTimeout(2, TimeUnit.MINUTES)
                    .withSetting(PopularProperties.TRANSPORT_TCP_PORT, EL_TEST_PORT)
                    .withSetting(PopularProperties.CLUSTER_NAME, EL_TEST_CLUSTER)
                    .withCleanInstallationDirectoryOnStop(false)
                    .build()
                    .start();
        } catch (Exception e) {
            Assert.fail("Failed to start elasticsearch");
            stopEmbeddedElasticsearch();
        }
    }

    public boolean isStarted() {
        return embeddedElastic != null;
    }

    @PreDestroy
    public void stopEmbeddedElasticsearch() {
        if(embeddedElastic != null) {
            embeddedElastic.stop();
        }
    }
}
