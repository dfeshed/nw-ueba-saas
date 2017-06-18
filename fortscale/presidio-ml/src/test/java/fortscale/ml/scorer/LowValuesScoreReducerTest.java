package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.config.ReductionConfiguration;
import fortscale.ml.scorer.record.JsonAdeRecord;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ScorerTestsContext.class)
public class LowValuesScoreReducerTest {
	@Autowired
	private FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

	private static final String DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME = "myLowValuesScoreReducer";

	private static Scorer getScorer(double score) throws Exception {
		FeatureScore featureScore = new FeatureScore("scorer", score);
		Scorer scorer = mock(Scorer.class);
		when(scorer.calculateScore(any(AdeRecord.class))).thenReturn(featureScore);
		return scorer;
	}

	private static ReductionConfiguration getConfig(
			String reducingFeatureName, double reducingFactor,
			double maxValueForFullyReduce, double minValueForNotReduce) {

		ReductionConfiguration reductionConfig = new ReductionConfiguration();
		reductionConfig.setReducingFeatureName(reducingFeatureName);
		reductionConfig.setReducingFactor(reducingFactor);
		reductionConfig.setMaxValueForFullyReduce(maxValueForFullyReduce);
		reductionConfig.setMinValueForNotReduce(minValueForNotReduce);
		return reductionConfig;
	}

	private LowValuesScoreReducer getReducer(
			String name, Scorer baseScorer, ReductionConfiguration... reductionConfigs) {

		return new LowValuesScoreReducer(name, baseScorer, Arrays.asList(reductionConfigs), recordReaderFactoryService);
	}

	private static AdeRecord getRecord(String featureName, Object featureValue) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(featureName, featureValue);
		return new JsonAdeRecord(Instant.now(), jsonObject);
	}

	@Test
	public void low_value_not_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(90.0),
				getConfig("readBytes", 0.8, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("writeBytes", 50000000.0));
		Assert.assertEquals(90.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(80.0),
				getConfig("readBytes", 0.8, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("readBytes", 100000000.0));
		Assert.assertEquals(64.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_max_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(70.0),
				getConfig("writeBytes", 0.7, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("writeBytes", 100000000.0));
		Assert.assertEquals(49.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_half_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(60.0),
				getConfig("readBytes", 0.7, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("readBytes", 300000000.0));
		Assert.assertEquals(51.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_min_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(100.0),
				getConfig("writeBytes", 0.5, 100000000.0, 500000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("writeBytes", 500000000.0));
		Assert.assertEquals(100.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(99.0),
				getConfig("totalBytes", 0.5, 200000000.0, 400000000.0));
		FeatureScore actual = reducer.calculateScore(getRecord("totalBytes", 600000000.0));
		Assert.assertEquals(99.0, actual.getScore(), 0);
	}
}
