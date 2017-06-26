package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.record.JsonAdeRecord;
import fortscale.ml.scorer.record.JsonAdeRecordReader;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinearScoreReducerTest {
	@Test
	public void linear_score_reducer_should_reduce_by_25_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(100), 0.75);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent());
		Assert.assertEquals(75, featureScore.getScore(), 0);
	}

	@Test
	public void linear_score_reducer_should_reduce_by_50_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(90), 0.5);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent());
		Assert.assertEquals(45, featureScore.getScore(), 0);
	}

	@Test
	public void linear_score_reducer_should_reduce_by_75_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(80), 0.25);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent());
		Assert.assertEquals(20, featureScore.getScore(), 0);
	}

	private static Scorer getReducedScorerMock(double reducedScore) throws Exception {
		Scorer reducedScorer = mock(Scorer.class);
		when(reducedScorer.calculateScore(any(JsonAdeRecordReader.class))).thenReturn(new FeatureScore("myReducedScorer", reducedScore));
		return reducedScorer;
	}

	private static JsonAdeRecordReader getDummyEvent() {
		return new JsonAdeRecordReader(new JsonAdeRecord(Instant.now(), new JSONObject()));
	}
}
