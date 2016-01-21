package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelsCacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-conf-service-test-context.xml"})
public class ScorerServiceTest {
    ScorersService scorersService;

    @Test
    public void ScorerServiceTest() {
        this.scorersService = new ScorersService(new ModelsCacheService() {
            @Override
            public Model getModel(Feature feature, String modelConfName, Map<String, Feature> context, long eventEpochtime) {
                return null;
            }

            @Override
            public void window() {

            }

            @Override
            public void close() {

            }
        });
    }
}
