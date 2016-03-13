package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.event.EventMessage;
import fortscale.ml.scorer.config.ReductionConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LowValuesScoreReducerTest extends ScorerBaseTest {
	private static final String CONFIG_FORMAT = configFormat();
	private static final String SCORER_NAME = "LowValuesScoreReducerTestScorer";
	private static final String BASE_SCORER_NAME = "LowValuesScoreReducerTestBaseScorer";


	@Before
	public void setUp() {
		super.setUp();
	}

	private static String configFormat() {
		String nameFormat = "\"reducingFeatureName\":\"%s\"";
		String factorFormat = "\"reducingFactor\":%.1f";
		String maxFormat = "\"maxValueForFullyReduce\":%.1f";
		String minFormat = "\"minValueForNotReduce\":%.1f";
		return String.format("{%s,%s,%s,%s}", nameFormat, factorFormat, maxFormat, minFormat);
	}

	private static String configsSingleton(String member) {
		return String.format("[%s]", member);
	}

	private LowValuesScoreReducer buildScorer(String reductionConfigs, double score) throws Exception {
		when(config.get(String.format("fortscale.score.%s.scorer", SCORER_NAME))).thenReturn(LowValuesScoreReducerFactory.SCORER_TYPE);
		when(config.get(String.format("fortscale.score.%s.output.field.name", SCORER_NAME))).thenReturn("output.field.name.scorer");
		when(config.get(String.format("fortscale.score.%s.base.scorer", SCORER_NAME))).thenReturn(BASE_SCORER_NAME);
		when(config.get(String.format("fortscale.score.%s.reduction.configs", SCORER_NAME))).thenReturn(reductionConfigs);

		ContstantRegexScorer baseScorer = mock(ContstantRegexScorer.class);
		FeatureScore featureScore = new FeatureScore("scoreName", score, null);
		when(baseScorer.calculateScore(any(EventMessage.class))).thenReturn(featureScore);
		context.setBean(BASE_SCORER_NAME, baseScorer);
		return (LowValuesScoreReducer)context.resolve(LowValuesScoreReducer.class, SCORER_NAME);
	}

	@Test
	public void should_serialize_reduction_configs_to_json() throws Exception {
		// Arrange
		String name = "readBytes";
		double factor = 0.8;
		double max = 100.0;
		double min = 500.0;

		ReductionConfiguration config = new ReductionConfiguration();
		config.setReducingFeatureName(name);
		config.setReducingFactor(factor);
		config.setMaxValueForFullyReduce(max);
		config.setMinValueForNotReduce(min);
		List<ReductionConfiguration> configs = Collections.singletonList(config);

		// Act
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(configs);

		// Assert
		assertNotNull(json);
		String expectedConfig = String.format(CONFIG_FORMAT, name, factor, max, min);
		String expectedConfigs = configsSingleton(expectedConfig);
		assertEquals(expectedConfigs, json);
	}

	@Test
	public void should_deserialize_json_to_reduction_configs() throws Exception {
		// Arrange
		String name = "readBytes";
		Double factor = 0.8;
		Double max = 100.0;
		Double min = 500.0;
		String configJson = String.format(CONFIG_FORMAT, name, factor, max, min);
		String configsJson = configsSingleton(configJson);

		// Act
		ObjectMapper mapper = new ObjectMapper();
		List<ReductionConfiguration> configs = Arrays.asList(mapper.readValue(configsJson, ReductionConfiguration[].class));

		// Assert
		assertNotNull(configs);
		assertEquals(1, configs.size());
		ReductionConfiguration config = configs.get(0);
		assertNotNull(config);
		assertEquals(name, config.getReducingFeatureName());
		assertEquals(factor, new Double(config.getReducingFactor()));
		assertEquals(max, new Double(config.getMaxValueForFullyReduce()));
		assertEquals(min, new Double(config.getMinValueForNotReduce()));
	}

	@Test
	public void low_value_not_present_no_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.8, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 90.0);
		EventMessage eventMessage = buildEventMessage(true, "writeBytes", 50000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(90.0), featureScore.getScore());
	}

	@Test public void low_value_present_full_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.8, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 80.0);
		EventMessage eventMessage = buildEventMessage(true, "readBytes", 100000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(64.0), featureScore.getScore());
	}

	@Test public void low_value_is_max_full_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "writeBytes", 0.7, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 70.0);
		EventMessage eventMessage = buildEventMessage(true, "writeBytes", 100000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(49.0), featureScore.getScore());
	}

	@Test public void low_value_present_half_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.7, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 60.0);

		EventMessage eventMessage = buildEventMessage(true, "readBytes", 300000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(51.0), featureScore.getScore());
	}

	@Test public void low_value_is_min_no_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "writeBytes", 0.5, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 100.0);
		EventMessage eventMessage = buildEventMessage(true, "writeBytes", 500000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(100.0), featureScore.getScore());
	}

	@Test public void low_value_present_no_reduction() throws Exception {
		// Arrange
		String configJson = String.format(CONFIG_FORMAT, "totalBytes", 0.5, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 99.0);
		EventMessage eventMessage = buildEventMessage(true, "totalBytes", 600000000.0);

		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(99.0), featureScore.getScore());
	}
}
