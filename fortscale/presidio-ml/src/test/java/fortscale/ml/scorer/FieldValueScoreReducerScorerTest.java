package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.params.FieldValueScoreReducerScorerConfParams;
import fortscale.ml.scorer.record.TestAdeRecord;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FieldValueScoreReducerScorerTest {
	@Test
	public void constructor_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams();
		params.getScorer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_with_null_name_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName(null);
		params.getScorer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_with_empty_name_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName("");
		params.getScorer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_with_blank_name_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName(" ");
		params.getScorer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_with_null_limiters_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setLimiters(null);
		params.getScorer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_with_null_base_scorer_test() {
		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setBaseScorer((Scorer)null);
		params.getScorer();
	}

	@Test
	public void one_limiter_so_there_should_be_a_reduction() throws Exception {
		FieldValueScoreLimiter limiter = new FieldValueScoreLimiter();
		limiter.setFieldName("city");
		Map<String, Integer> valueToMaxScoreMap = new HashMap<>();
		valueToMaxScoreMap.put("London", 50);
		limiter.setValueToMaxScoreMap(valueToMaxScoreMap);
		List<FieldValueScoreLimiter> limiterList = new ArrayList<>();
		limiterList.add(limiter);

		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setLimiters(limiterList).setBaseScorer(80.0);
		FieldValueScoreReducerScorer reducer = params.getScorer();

		// Create event message
		AdeRecordReader adeRecordReader = new TestAdeRecord().setCity("London").setSourceIp("3.3.3.3").getAdeRecordReader();

		// Act
		FeatureScore featureScore = reducer.calculateScore(adeRecordReader);

		// Assert
		assertEquals(new Double(40), featureScore.getScore());
	}

	@Test
	public void two_limiters_but_should_reduce_according_to_first() throws Exception {
		FieldValueScoreLimiter limiter = new FieldValueScoreLimiter();
		limiter.setFieldName("city");
		Map<String, Integer> valueToMaxScoreMap = new HashMap<>();
		valueToMaxScoreMap.put("London", 50);
		limiter.setValueToMaxScoreMap(valueToMaxScoreMap);

		FieldValueScoreLimiter limiter2 = new FieldValueScoreLimiter();
		limiter2.setFieldName("sourceIp");
		valueToMaxScoreMap.put("3.3.3.3", 33);
		limiter.setValueToMaxScoreMap(valueToMaxScoreMap);

		List<FieldValueScoreLimiter> limiterList = new ArrayList<>();
		limiterList.add(limiter);
		limiterList.add(limiter2);

		// Create scorer
		FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setLimiters(limiterList).setBaseScorer(70.0);
		FieldValueScoreReducerScorer reducer = params.getScorer();

		// Create event message
		AdeRecordReader adeRecordReader = new TestAdeRecord().setCity("London").setSourceIp("3.3.3.3").getAdeRecordReader();

		// Act
		FeatureScore featureScore = reducer.calculateScore(adeRecordReader);

		// Assert
		assertEquals(new Double(35), featureScore.getScore());
	}
}
