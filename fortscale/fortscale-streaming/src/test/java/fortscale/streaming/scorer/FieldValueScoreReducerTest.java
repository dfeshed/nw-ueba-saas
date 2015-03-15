package fortscale.streaming.scorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.scorer.FieldValueScoreLimiters.FieldValueScoreLimiter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldValueScoreReducerTest extends ScorerBaseTest {
	private static final String SCORER_NAME = "FieldValueScoreReducerTestScorer";
	private static final String OUTPUT_FIELD_NAME = "FieldValueScoreReducerTestOutputField";
	private static final String BASE_SCORER_NAME = "FieldValueScoreReducerTestBaseScorer";
	private static final String SCORE_NAME = "FieldValueScoreReducerTestScore";

	private static final String LIMITER_FORMAT = "{\"fieldName\":\"%s\",\"valueToMaxScoreMap\":{%s}}";
	private static final String LIMITERS_FORMAT = "{\"limiters\":[%s]}";

	private ScorerContext context;

	@Before
	public void setUp() {
		super.setUp();
		context = new ScorerContext(config);
	}

	private String buildJson(String... args) {
		assertEquals(0, args.length % 2);

		String limitersList = "";
		for (int i = 0; i < args.length; i += 2) {
			limitersList += String.format(LIMITER_FORMAT, args[i], args[i + 1]);
			if (i < args.length - 2) limitersList += ",";
		}

		return String.format(LIMITERS_FORMAT, limitersList);
	}

	private FieldValueScoreReducer buildScorer(String limiters, Double score) throws Exception {
		when(config.get(String.format("fortscale.score.%s.scorer", SCORER_NAME))).thenReturn(FieldValueScoreReducerFactory.SCORER_TYPE);
		when(config.get(String.format("fortscale.score.%s.output.field.name", SCORER_NAME))).thenReturn(OUTPUT_FIELD_NAME);
		when(config.get(String.format("fortscale.score.%s.base.scorer", SCORER_NAME))).thenReturn(BASE_SCORER_NAME);
		when(config.get(String.format("fortscale.score.%s.limiters", SCORER_NAME))).thenReturn(limiters);

		ContstantRegexScorer baseScorer = mock(ContstantRegexScorer.class);
		context.setBean(BASE_SCORER_NAME, baseScorer);

		FeatureScore featureScore = new FeatureScore(SCORE_NAME, score, null);
		when(baseScorer.calculateScore(any(EventMessage.class))).thenReturn(featureScore);

		return (FieldValueScoreReducer)context.resolve(FieldValueScoreReducer.class, SCORER_NAME);
	}

	@Test
	public void should_serialize_field_value_score_limiters_to_json_config() throws Exception {
		// Arrange
		FieldValueScoreLimiters limiters = new FieldValueScoreLimiters();
		limiters.setLimiters(new ArrayList<FieldValueScoreLimiter>(2));

		String name = "country";
		Map<String, Integer> map = new HashMap<>();
		map.put("Canada", 40);

		FieldValueScoreLimiter limiter = new FieldValueScoreLimiter();
		limiter.setFieldName(name);
		limiter.setValueToMaxScoreMap(map);
		limiters.getLimiters().add(limiter);

		name = "source_ip";
		map = new HashMap<>();
		map.put("5.5.5.5", 60);

		limiter = new FieldValueScoreLimiter();
		limiter.setFieldName(name);
		limiter.setValueToMaxScoreMap(map);
		limiters.getLimiters().add(limiter);

		// Act
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(limiters);

		// Assert
		assertNotNull(json);
		String expected = buildJson(
			"country", "\"Canada\":40",
			"source_ip", "\"5.5.5.5\":60");
		assertEquals(expected, json);
	}

	@Test
	public void should_deserialize_json_config_to_field_value_score_limiters() throws Exception {
		// Arrange
		String json = buildJson(
			"city", "\"Paris\":30",
			"local_ip", "\"7.7.7.7\":70");

		// Act
		ObjectMapper mapper = new ObjectMapper();
		FieldValueScoreLimiters limiters = mapper.readValue(json, FieldValueScoreLimiters.class);

		// Assert
		assertNotNull(limiters);
		assertNotNull(limiters.getLimiters());
		assertEquals(2, limiters.getLimiters().size());

		FieldValueScoreLimiter limiter = limiters.getLimiters().get(0);
		assertNotNull(limiter);
		assertNotNull(limiter.getFieldName());
		assertEquals("city", limiter.getFieldName());
		assertNotNull(limiter.getValueToMaxScoreMap());
		assertEquals(1, limiter.getValueToMaxScoreMap().size());
		assertEquals(new Integer(30), limiter.getValueToMaxScoreMap().get("Paris"));

		limiter = limiters.getLimiters().get(1);
		assertNotNull(limiter);
		assertNotNull(limiter.getFieldName());
		assertEquals("local_ip", limiter.getFieldName());
		assertNotNull(limiter.getValueToMaxScoreMap());
		assertEquals(1, limiter.getValueToMaxScoreMap().size());
		assertEquals(new Integer(70), limiter.getValueToMaxScoreMap().get("7.7.7.7"));
	}

	@Test
	public void no_limiters_so_there_should_be_no_reduction() throws Exception {
		// Create scorer
		String json = buildJson(
			"city", "\"London\":50",
			"source_ip", "\"3.3.3.3\":75");
		FieldValueScoreReducer reducer = buildScorer(json, 100.0);

		// Create event message
		JSONObject eventJson = new JSONObject();
		eventJson.put("city", "Paris");
		eventJson.put("local_ip", "3.3.3.3");

		// Act
		FeatureScore featureScore = reducer.calculateScore(new EventMessage(eventJson));

		// Assert
		assertEquals(new Double(100), featureScore.getScore());
	}
}
