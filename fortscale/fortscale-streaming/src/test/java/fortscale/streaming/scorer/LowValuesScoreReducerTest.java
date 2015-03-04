package fortscale.streaming.scorer;

import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static fortscale.streaming.scorer.ReductionConfigurations.ReductionConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LowValuesScoreReducerTest {
	@Mock
	protected Config config;
	@Mock
	protected Scorer baseScorer;
	@Mock
	protected ReductionConfigurations reductionConfigs;

	private LowValuesScoreReducer reducer;

	@Test
	public void low_values_score_reducer_should_reduce_scores_correctly() throws Exception {
		// Setup
		when(config.get(any(String.class))).thenReturn("outputFieldName");
		reducer = new LowValuesScoreReducer("scorerName", config);
		reducer.baseScorer = baseScorer;
		reducer.reductionConfigs = reductionConfigs;

		FeatureScore featureScore = new FeatureScore("scoreName", 90.0, null);
		when(baseScorer.calculateScore(any(EventMessage.class))).thenReturn(featureScore);

		String reducingValueName = "readBytes";
		double reductionFactor = 0.8;
		double maxValueForFullReduction = 100000000; // 100MB
		double minValueForNoReduction = 500000000; // 500MB
		ReductionConfiguration reductionConfig = new ReductionConfiguration(reducingValueName, reductionFactor, maxValueForFullReduction, minValueForNoReduction);

		List<ReductionConfiguration> listOfReductionConfigs = new ArrayList<>();
		listOfReductionConfigs.add(reductionConfig);
		when(reductionConfigs.getReductionConfigs()).thenReturn(listOfReductionConfigs);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange - reducer field not present, no reduction
		json.put("writeBytes", 50000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(90.0), featureScore.getScore());

		// Arrange - reducer field present, full reduction 1
		json.put("readBytes", 50000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(72.0), featureScore.getScore());

		// Arrange - reducer field present, full reduction 2
		json.put("readBytes", 100000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(72.0), featureScore.getScore());

		// Arrange - reducer field present, half reduction
		json.put("readBytes", 300000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(81.0), featureScore.getScore());

		// Arrange - reducer field present, no reduction 1
		json.put("readBytes", 500000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(90.0), featureScore.getScore());

		// Arrange - reducer field present, no reduction 2
		json.put("readBytes", 750000000);
		// Act
		featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(90.0), featureScore.getScore());
	}
}
