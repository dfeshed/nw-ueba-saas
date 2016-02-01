package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.ml.scorer.config.ReductionConfigurations;
import fortscale.ml.scorer.config.ReductionConfigurations.ReductionConfiguration;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-conf-service-test-context.xml"})
public class LowValuesScoreReducerTest {
	private static final String DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME = "myLowValuesScoreReducer";
	private static final long DEFAULT_EVENT_EPOCHTIME = 1451606400;

	private static Scorer getScorer(double score) throws Exception {
		FeatureScore featureScore = new FeatureScore("scorer", score);
		Scorer scorer = mock(Scorer.class);
		when(scorer.calculateScore(any(Event.class), anyLong())).thenReturn(featureScore);
		return scorer;
	}

	private static ReductionConfigurations getConfigs(
			String reducingFeatureName, double reducingFactor,
			double maxValueForFullyReduce, double minValueForNotReduce) {

		ReductionConfiguration reductionConfig = new ReductionConfiguration();
		reductionConfig.setReducingFeatureName(reducingFeatureName);
		reductionConfig.setReducingFactor(reducingFactor);
		reductionConfig.setMaxValueForFullyReduce(maxValueForFullyReduce);
		reductionConfig.setMinValueForNotReduce(minValueForNotReduce);
		ReductionConfigurations reductionConfigs = new ReductionConfigurations();
		reductionConfigs.setReductionConfigs(Collections.singletonList(reductionConfig));
		return reductionConfigs;
	}

	private static LowValuesScoreReducer getReducer(
			String name, Scorer baseScorer, ReductionConfigurations reductionConfigs) {

		return new LowValuesScoreReducer(name, baseScorer, reductionConfigs);
	}

	private static Event getEvent(String featureName, Object featureValue) {
		Event event = mock(Event.class);
		when(event.get(featureName)).thenReturn(featureValue);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(featureName, featureValue);
		when(event.getJSONObject()).thenReturn(jsonObject);
		return event;
	}

	@Test
	public void low_value_not_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(90.0),
				getConfigs("readBytes", 0.8, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("writeBytes", 50000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(90.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(80.0),
				getConfigs("readBytes", 0.8, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("readBytes", 100000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(64.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_max_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(70.0),
				getConfigs("writeBytes", 0.7, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("writeBytes", 100000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(49.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_half_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(60.0),
				getConfigs("readBytes", 0.7, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("readBytes", 300000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(51.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_min_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(100.0),
				getConfigs("writeBytes", 0.5, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("writeBytes", 500000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(100.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(99.0),
				getConfigs("totalBytes", 0.5, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getEvent("totalBytes", 600000000.0), DEFAULT_EVENT_EPOCHTIME);
		Assert.assertEquals(99.0, actual.getScore(), 0);
	}
}
