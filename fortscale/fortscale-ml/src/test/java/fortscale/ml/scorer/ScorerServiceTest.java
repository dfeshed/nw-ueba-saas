package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.factory.ScorersFactoryService;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Assert;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-conf-service-test-context.xml"})
public class ScorerServiceTest {

    @Autowired
    ScorersFactoryService scorersFactoryService;
    @Autowired
    ScorersService scorersService;

    @Test
    public void ScorerServiceTest() throws Exception {
        JSONObject event = new JSONObject();
        long eventTime = 1453334400L; //2016-01-21T00:00:00
        String dataSource = "4769";
        List<FeatureScore> featureScores = scorersService.calculateScores(event, eventTime, dataSource);

        Assert.assertNotNull(featureScores);
        Assert.assertEquals(1, featureScores.size());
    }
}
