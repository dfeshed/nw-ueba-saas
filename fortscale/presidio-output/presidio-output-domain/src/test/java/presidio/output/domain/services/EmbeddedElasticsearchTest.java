package presidio.output.domain.services;

import fortscale.utils.elasticsearch.config.EmbeddedElasticsearchInitialiser;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.spring.TestConfig;

/**
 * Created by efratn on 23/10/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class EmbeddedElasticsearchTest {

    @Autowired
    protected EmbeddedElasticsearchInitialiser embeddedElasticsearchInitialiser;
}