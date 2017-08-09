package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.config.ReductionConfiguration;
import fortscale.ml.scorer.record.TestAdeRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class LowValuesScoreReducerTest {
	private static final String DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME = "myLowValuesScoreReducer";

	private static Scorer getScorer(double score) throws Exception {
		FeatureScore featureScore = new FeatureScore("scorer", score);
		Scorer scorer = mock(Scorer.class);
		when(scorer.calculateScore(any(AdeRecordReader.class))).thenReturn(featureScore);
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

		return new LowValuesScoreReducer(name, baseScorer, Arrays.asList(reductionConfigs));
	}

	@Test
	public void low_value_not_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(90.0),
				getConfig("readBytes", 0.8, 100000000.0, 500000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setWriteBytes(50000000.0).setReadBytes(null).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(90.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(80.0),
				getConfig("readBytes", 0.8, 200000000.0, 400000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setReadBytes(100000000.0).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(64.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_max_full_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(70.0),
				getConfig("writeBytes", 0.7, 100000000.0, 500000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setWriteBytes(100000000.0).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(49.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_half_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(60.0),
				getConfig("readBytes", 0.7, 200000000.0, 400000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setReadBytes(300000000.0).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(51.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_is_min_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(100.0),
				getConfig("writeBytes", 0.5, 100000000.0, 500000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setWriteBytes(500000000.0).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(100.0, actual.getScore(), 0);
	}

	@Test
	public void low_value_present_no_reduction() throws Exception {
		LowValuesScoreReducer reducer = getReducer(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getScorer(99.0),
				getConfig("totalBytes", 0.5, 200000000.0, 400000000.0));
		AdeRecordReader adeRecordReader = new TestAdeRecord().setTotalBytes(600000000.0).getAdeRecordReader();
		FeatureScore actual = reducer.calculateScore(adeRecordReader);
		Assert.assertEquals(99.0, actual.getScore(), 0);
	}
}
