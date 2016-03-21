package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinearScoreReducerTest {
	@Test
	public void linear_score_reducer_should_reduce_by_25_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(100), 0.75);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent(), 1451606400);
		Assert.assertEquals(75, featureScore.getScore(), 0);
	}

	@Test
	public void linear_score_reducer_should_reduce_by_50_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(90), 0.5);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent(), 1451606400);
		Assert.assertEquals(45, featureScore.getScore(), 0);
	}

	@Test
	public void linear_score_reducer_should_reduce_by_75_percent() throws Exception {
		Scorer scorer = new LinearScoreReducer("myLinearScoreReducer", getReducedScorerMock(80), 0.25);
		FeatureScore featureScore = scorer.calculateScore(getDummyEvent(), 1451606400);
		Assert.assertEquals(20, featureScore.getScore(), 0);
	}

	private static Scorer getReducedScorerMock(double reducedScore) throws Exception {
		Scorer reducedScorer = mock(Scorer.class);
		when(reducedScorer.calculateScore(any(Event.class), anyLong()))
				.thenReturn(new FeatureScore("myReducedScorer", reducedScore));
		return reducedScorer;
	}

	private static Event getDummyEvent() {
		return new EventMessage(new JSONObject());
	}
}
