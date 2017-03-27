package fortscale.ml.scorer;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.RawEvent;
import fortscale.domain.core.FeatureScore;
import fortscale.domain.core.FeatureScoreList;
import fortscale.ml.scorer.config.TestScorerConfService;
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
    @Autowired
    private TestScorerConfService scorerConfService;

    @Test
    public void scorerServiceTest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        String dataSource = scorerConfService.getAllDataSourceScorerConfs().keySet().stream().findFirst().get();
        jsonObject.put("failure_code", "0x12");
        RawEvent rawEvent = new RawEvent(jsonObject, dataEntitiesConfigWithBlackList, dataSource);
        long eventTime = 1453334400L; // 2016-01-21T00:00:00
        List<FeatureScore> featureScores = scorersService.calculateScores(rawEvent, eventTime);
        Assert.assertNotNull(featureScores);
        Assert.assertEquals(1, featureScores.size());
        Assert.assertTrue(featureScores instanceof FeatureScoreList); //without it the root FeatureScore will not contain the type when it is serialized to json.


    }
}
