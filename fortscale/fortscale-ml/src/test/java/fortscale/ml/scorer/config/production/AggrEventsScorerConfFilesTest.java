package fortscale.ml.scorer.config.production;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.TestScorerConfService;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		loader = AnnotationConfigContextLoader.class,
		classes = {ProductionScorerConfFilesTestContext.class, AggrEventsScorerConfFilesTest.ContextConfiguration.class}
)
public class AggrEventsScorerConfFilesTest {
	@Configuration
	static class ContextConfiguration {
		@Bean
		public ScorerConfService scorerConfService() {
			return new TestScorerConfService("classpath:config/asl/scorers/aggregation-events/*.json");
		}
	}

	@Autowired
	private ScorerConfService scorerConfService;

	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Test
	public void validateAllScorerConfs() {
		int counter = ProductionScorerConfFilesTest.validateAllScorerConfs(scorerConfService, scorerFactoryService);
		Assert.assertEquals(64, counter);
	}
}
