package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static fortscale.streaming.scorer.ReductionConfigurations.ReductionConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class LowValuesScoreReducerTest extends ScorerBaseTest {
	private static final String CONFIG_FORMAT = configFormat();
	private static final String SCORER_NAME = "LowValuesScoreReducerTestScorer";
	private static final String BASE_SCORER_NAME = "LowValuesScoreReducerTestBaseScorer";

	private ScorerContext context;

	@Before
	public void setUp() {
		super.setUp();
		context = new ScorerContext(config);
	}

	private static String configFormat() {
		String nameFormat = "\"reducingValueName\":\"%s\"";
		String factorFormat = "\"reductionFactor\":%.1f";
		String maxFormat = "\"maxValueForFullReduction\":%.1f";
		String minFormat = "\"minValueForNoReduction\":%.1f";
		return String.format("{%s,%s,%s,%s}", nameFormat, factorFormat, maxFormat, minFormat);
	}

	private static String configsSingleton(String member) {
		return String.format("{\"reductionConfigs\":[%s]}", member);
	}

	private LowValuesScoreReducer buildScorer(String reductionConfigs, double score) throws Exception {
		when(config.get(String.format("fortscale.score.%s.scorer", SCORER_NAME))).thenReturn(LowValuesScoreReducerFactory.SCORER_TYPE);
		when(config.get(String.format("fortscale.score.%s.output.field.name", SCORER_NAME))).thenReturn("output.field.name.scorer");
		when(config.get(String.format("fortscale.score.%s.base.scorer", SCORER_NAME))).thenReturn(BASE_SCORER_NAME);
		when(config.get(String.format("fortscale.score.%s.reduction.configs", SCORER_NAME))).thenReturn(reductionConfigs);

		context.setBean(BASE_SCORER_NAME, mock(ContstantRegexScorer.class));
		LowValuesScoreReducer reducer = (LowValuesScoreReducer)context.resolve(LowValuesScoreReducer.class, SCORER_NAME);

		FeatureScore featureScore = new FeatureScore("scoreName", score, null);
		when(reducer.getBaseScorer().calculateScore(any(EventMessage.class))).thenReturn(featureScore);

		return reducer;
	}

	@Test
	public void should_serialize_reduction_configs_to_json() throws Exception {
		// Arrange
		String name = "readBytes";
		double factor = 0.8;
		double max = 100.0;
		double min = 500.0;

		ReductionConfiguration config = new ReductionConfiguration();
		config.setReducingValueName(name);
		config.setReductionFactor(factor);
		config.setMaxValueForFullReduction(max);
		config.setMinValueForNoReduction(min);

		ReductionConfigurations configs = new ReductionConfigurations();
		configs.setReductionConfigs(new ArrayList<ReductionConfiguration>());
		configs.getReductionConfigs().add(config);

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
		ReductionConfigurations configs = mapper.readValue(configsJson, ReductionConfigurations.class);

		// Assert
		assertNotNull(configs);
		assertNotNull(configs.getReductionConfigs());
		assertEquals(1, configs.getReductionConfigs().size());
		ReductionConfiguration config = configs.getReductionConfigs().get(0);
		assertNotNull(config);
		assertEquals(name, config.getReducingValueName());
		assertEquals(factor, new Double(config.getReductionFactor()));
		assertEquals(max, new Double(config.getMaxValueForFullReduction()));
		assertEquals(min, new Double(config.getMinValueForNoReduction()));
	}

	@Test
	public void low_value_not_present_no_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.8, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 90.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("writeBytes", 50000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(90.0), featureScore.getScore());
	}

	@Test public void low_value_present_full_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.8, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 80.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("readBytes", 100000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(64.0), featureScore.getScore());
	}

	@Test public void low_value_is_max_full_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "writeBytes", 0.7, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 70.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("writeBytes", 100000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(49.0), featureScore.getScore());
	}

	@Test public void low_value_present_half_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "readBytes", 0.7, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 60.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("readBytes", 300000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(51.0), featureScore.getScore());
	}

	@Test public void low_value_is_min_no_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "writeBytes", 0.5, 100000000.0, 500000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 100.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("writeBytes", 500000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(100.0), featureScore.getScore());
	}

	@Test public void low_value_present_no_reduction() throws Exception {
		String configJson = String.format(CONFIG_FORMAT, "totalBytes", 0.5, 200000000.0, 400000000.0);
		LowValuesScoreReducer reducer = buildScorer(configsSingleton(configJson), 99.0);

		JSONObject json = new JSONObject();
		EventMessage eventMessage = new EventMessage(json);

		// Arrange
		json.put("totalBytes", 600000000.0);
		// Act
		FeatureScore featureScore = reducer.calculateScore(eventMessage);
		// Assert
		assertEquals(new Double(99.0), featureScore.getScore());
	}
}
