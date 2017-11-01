package fortscale.utils.elasticsearch.config;

import fortscale.utils.logging.Logger;
import org.junit.Assert;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Manage embedded elasticsearch for test purpose.
 * EmbeddedElasticsearchInitialiser bean must be initialized by spring before the elasticsearch client is created
 *
 * Created by efratn on 17/10/2017.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EmbeddedElasticsearchInitialiser {

    private final Logger logger = Logger.getLogger(EmbeddedElasticsearchInitialiser.class);

    public static String EL_TEST_VERSION = "5.0.0";
    public static String EL_TEST_PORT = "9350";
    public static String EL_TEST_CLUSTER = "fortscalse_test";

    private EmbeddedElastic embeddedElastic = null;

    @PostConstruct
    public void setupEmbeddedElasticsearch() {
        if(embeddedElastic != null) {
//            System.out.println("embedded elasticsearch already started, skipping startup");
            logger.debug("embedded elasticsearch already started, skipping startup");
            return;
        }
//        System.out.println("starting embedded elasticsearch");
        logger.debug("starting embedded elasticsearch");
        try {
            embeddedElastic = EmbeddedElastic.builder()
                    .withElasticVersion(EL_TEST_VERSION)
                    .withStartTimeout(2, TimeUnit.MINUTES)
                    .withSetting(PopularProperties.TRANSPORT_TCP_PORT, EL_TEST_PORT)
                    .withSetting(PopularProperties.CLUSTER_NAME, EL_TEST_CLUSTER)
                    .withCleanInstallationDirectoryOnStop(true)
                    .build()
                    .start();
        } catch (Exception e) {
//            stopEmbeddedElasticsearch();
            embeddedElastic = null;
            Assert.fail("Failed to start elasticsearch");
        }
    }

    @PreDestroy
    public void stopEmbeddedElasticsearch() {
//        System.out.println("stopping embedded elasticsearch");
        logger.debug("stopping embedded elasticsearch");
        embeddedElastic.stop();

    }

}
