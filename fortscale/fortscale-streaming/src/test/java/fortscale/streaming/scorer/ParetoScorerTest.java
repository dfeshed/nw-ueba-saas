package fortscale.streaming.scorer;

import org.apache.samza.config.ConfigException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.when;

public class ParetoScorerTest extends ScorerContainerTest {
	protected double highestScoreWeight;

	@Before
	public void setUp() {
		super.setUp();
		delta = 0.0001d;
	}

	@Override
	protected Scorer buildScorer(String scorerType, String scorerName, String outputFieldName, List<String> scorers) {
		when(config.getDouble(String.format("fortscale.score.%s.highest.score.weight", scorerName))).thenReturn(highestScoreWeight);
		return super.buildScorer(scorerType, scorerName, outputFieldName, scorers);
	}

	@Test
	public void test_build_scorer_with_two_scores() throws Exception {
		highestScoreWeight = 0.8d;
		// Scores: 100, 90
		testBuildScorerWithScore1(ParetoScorerFactory.SCORER_TYPE, 98.0d);
	}

	@Test
	public void test_build_scorer_with_one_score() throws Exception {
		highestScoreWeight = 0.7d;
		// Scores: 90
		testBuildScorerWithScore2(ParetoScorerFactory.SCORER_TYPE, 63.0d);
	}

	@Test
	public void test_build_scorer_with_no_scores() throws Exception {
		highestScoreWeight = 0.8d;
		testBuildScorerWithScore3(ParetoScorerFactory.SCORER_TYPE, 0.0d);
	}

	@Test
	public void test_build_scorer_with_two_scores_reversed() throws Exception {
		highestScoreWeight = 0.7d;
		// Scores: 90, 100
		testBuildScorerWithScore4(ParetoScorerFactory.SCORER_TYPE, 97.0d);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_no_scorer_name() throws Exception {
		highestScoreWeight = 0.5d;
		testBuildScorerWithNoScorerName(ParetoScorerFactory.SCORER_TYPE);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_no_output_field_name() throws Exception {
		highestScoreWeight = 0.5d;
		testBuildScorerWithNonOutputFieldName(ParetoScorerFactory.SCORER_TYPE);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_blank_output_field_name() throws Exception {
		highestScoreWeight = 0.5d;
		testBuildScorerWithBlankOutputFieldName(ParetoScorerFactory.SCORER_TYPE);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_no_scorers() throws Exception {
		highestScoreWeight = 0.5d;
		testBuildScorerWithNonScorers(ParetoScorerFactory.SCORER_TYPE);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_blank_scorers() throws Exception {
		highestScoreWeight = 0.5d;
		testBuildScorerWithBlankScorers(ParetoScorerFactory.SCORER_TYPE);
	}

	@Test(expected = ConfigException.class)
	public void test_build_scorer_with_invalid_highest_score_weight() throws Exception {
		highestScoreWeight = -0.5d;
		buildScorer(ParetoScorerFactory.SCORER_TYPE, SCORER_NAME, OUTPUT_FIELD_NAME, configScorers());
	}
}
