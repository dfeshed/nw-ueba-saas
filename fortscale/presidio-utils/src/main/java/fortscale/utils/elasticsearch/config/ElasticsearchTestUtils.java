package fortscale.utils.elasticsearch.config;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.util.concurrent.TimeUnit;

/**
 * Created by efratn on 17/10/2017.
 */
public class ElasticsearchTestUtils {

    public static String EL_TEST_VERSION = "5.0.0";
    public static String EL_TEST_PORT = "9350";
    public static String EL_TEST_CLUSTER = "fortscalse_test";


    private EmbeddedElastic embeddedElastic;

    public void setupLocalElasticsearch() throws Exception {

        embeddedElastic = EmbeddedElastic.builder()
                .withElasticVersion(EL_TEST_VERSION)
                .withStartTimeout(30, TimeUnit.SECONDS)
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, EL_TEST_PORT)
                .withSetting(PopularProperties.CLUSTER_NAME, EL_TEST_CLUSTER)
                .build()
                .start();
    }

    public void stopEmbeddedElasticsearch() {
        if(embeddedElastic != null) {
            embeddedElastic.stop();
        }
    }
}
