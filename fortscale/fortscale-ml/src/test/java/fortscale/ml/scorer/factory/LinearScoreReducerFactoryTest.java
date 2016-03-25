package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.LinearScoreReducer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.LinearScoreReducerConf;
import fortscale.ml.scorer.config.LinearScoreReducerConfTest;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/spring/scorer-factory-tests-context.xml")
public class LinearScoreReducerFactoryTest {
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;
	@Autowired
	private LinearScoreReducerFactory linearScoreReducerFactory;

	@Test
	public void linear_score_reducer_factory_should_register_to_factory_service() {
		Factory<Scorer> scorerFactory = scorerFactoryService.getFactory(LinearScoreReducerConf.SCORER_TYPE);
		Assert.assertNotNull(scorerFactory);
		Assert.assertEquals(linearScoreReducerFactory, scorerFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void get_product_should_fail_when_factory_config_is_null() {
		linearScoreReducerFactory.getProduct(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void get_product_should_fail_when_factory_config_is_of_wrong_type() throws IOException {
		linearScoreReducerFactory.getProduct(LinearScoreReducerConfTest.getScorerConf(
				LinearScoreReducerConfTest.defaultReducedScorerConfJsonObject));
	}

	@Test
	public void get_product_should_return_the_correct_scorer() throws IOException {
		Scorer scorer = linearScoreReducerFactory.getProduct(LinearScoreReducerConfTest.getScorerConf(
				LinearScoreReducerConfTest.getLinearScoreReducerConfJsonObject(
						LinearScoreReducerConf.SCORER_TYPE, "myLinearScoreReducer",
						LinearScoreReducerConfTest.defaultReducedScorerConfJsonObject, 0.36)));
		Assert.assertNotNull(scorer);
		Assert.assertEquals(LinearScoreReducer.class, scorer.getClass());
	}
}
