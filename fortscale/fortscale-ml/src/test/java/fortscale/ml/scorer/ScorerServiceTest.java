package fortscale.ml.scorer;

import fortscale.utils.factory.FactoryService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-conf-service-test-context.xml"})
public class ScorerServiceTest {

    @Autowired
    FactoryService<Scorer> scorerFactoryService;
    @Autowired
    ScorersService scorersService;

    @Test
    public void scorerServiceTest() throws Exception {
        JSONObject event = new JSONObject();
        long eventTime = 1453334400L; //2016-01-21T00:00:00
        String dataSource = "kerberos_logins";
        List<FeatureScore> featureScores = scorersService.calculateScores(event, eventTime, dataSource);

        Assert.assertNotNull(featureScores);
        Assert.assertEquals(1, featureScores.size());
    }
}
