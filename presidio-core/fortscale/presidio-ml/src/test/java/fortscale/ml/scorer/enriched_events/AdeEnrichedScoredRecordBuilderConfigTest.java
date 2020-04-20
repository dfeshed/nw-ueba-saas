package fortscale.ml.scorer.enriched_events;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by YaronDL on 6/18/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdeEnrichedScoredRecordBuilderConfig.class)
public class AdeEnrichedScoredRecordBuilderConfigTest {

    @Autowired
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;

    @Test
    public void testContextLoadOfAdeEnrichedScoredRecordBuilderConfig(){
        Assert.assertNotNull(adeEnrichedScoredRecordBuilder);
    }
}
