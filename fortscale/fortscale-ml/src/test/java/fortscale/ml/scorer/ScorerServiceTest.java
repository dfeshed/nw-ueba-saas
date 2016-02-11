package fortscale.ml.scorer;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.RawEvent;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-service-test-context.xml"})
public class ScorerServiceTest {
    @Autowired
    private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;
    @Autowired
    private ScorersService scorersService;

    @Test
    public void scorerServiceTest() throws Exception {
        RawEvent rawEvent = new RawEvent(new JSONObject(), dataEntitiesConfigWithBlackList, "kerberos_logins");
        long eventTime = 1453334400L; // 2016-01-21T00:00:00
        List<FeatureScore> featureScores = scorersService.calculateScores(rawEvent, eventTime);

        Assert.assertNotNull(featureScores);
        Assert.assertEquals(1, featureScores.size());
    }
}
