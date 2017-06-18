package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TimeModelScorerConfTest {
	private static final String SCORER_TYPE = TimeModelScorerConf.SCORER_TYPE;
	private static final String DEFAULT_SCORER_NAME = "myScorer";
	private static final String DEFAULT_MODEL_NAME = "myModel";

	private static JSONObject getModelInfoJsonObject(String modelName) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", modelName);
		return jsonObject;
	}

	private static String getTimeModelScorerConfString(
			String scorerType, String name, JSONObject modelInfoJsonObject,
			Integer minNumOfSamplesToInfluence, Integer enoughNumOfSamplesToInfluence,
			Boolean isUseCertaintyToCalculateScore, Integer maxRareTimestampCount, Integer maxNumOfRareTimestamps) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", scorerType);
		jsonObject.put("name", name);
		jsonObject.put("model", modelInfoJsonObject);
		if (minNumOfSamplesToInfluence != null)
			jsonObject.put("min-number-of-samples-to-influence", minNumOfSamplesToInfluence);
		if (enoughNumOfSamplesToInfluence != null)
			jsonObject.put("number-of-samples-to-influence-enough", enoughNumOfSamplesToInfluence);
		if (isUseCertaintyToCalculateScore != null)
			jsonObject.put("use-certainty-to-calculate-score", isUseCertaintyToCalculateScore);
		if (maxRareTimestampCount != null)
			jsonObject.put("max-rare-timestamp-count", maxRareTimestampCount);
		if (maxNumOfRareTimestamps != null)
			jsonObject.put("max-num-of-rare-timestamps", maxNumOfRareTimestamps);
		return jsonObject.toJSONString();
	}

	private static IScorerConf getScorerConf(String jsonString) throws IOException {
		return new ObjectMapper().readValue(jsonString, IScorerConf.class);
	}

	@Test
	public void deserialize_json() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, true, 36, 42);

		IScorerConf scorerConf = getScorerConf(jsonString);
		Assert.assertNotNull(scorerConf);
		Assert.assertEquals(TimeModelScorerConf.class, scorerConf.getClass());

		TimeModelScorerConf conf = (TimeModelScorerConf)scorerConf;
		Assert.assertEquals(DEFAULT_SCORER_NAME, conf.getName());
		Assert.assertEquals(DEFAULT_MODEL_NAME, conf.getModelInfo().getModelName());
		Assert.assertEquals(50, conf.getMinNumOfSamplesToInfluence());
		Assert.assertEquals(100, conf.getEnoughNumOfSamplesToInfluence());
		Assert.assertEquals(true, conf.isUseCertaintyToCalculateScore());
		Assert.assertEquals(36, conf.getMaxRareTimestampCount());
		Assert.assertEquals(42, conf.getMaxNumOfRareTimestamps());
	}

	@Test
	public void deserialize_json_without_non_mandatory_fields() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				null, null, null, null, null);

		IScorerConf scorerConf = getScorerConf(jsonString);
		Assert.assertNotNull(scorerConf);
		Assert.assertEquals(TimeModelScorerConf.class, scorerConf.getClass());

		TimeModelScorerConf conf = (TimeModelScorerConf)scorerConf;
		Assert.assertEquals(DEFAULT_SCORER_NAME, conf.getName());
		Assert.assertEquals(DEFAULT_MODEL_NAME, conf.getModelInfo().getModelName());
		Assert.assertEquals(
				ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE,
				conf.getMinNumOfSamplesToInfluence());
		Assert.assertEquals(
				ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE,
				conf.getEnoughNumOfSamplesToInfluence());
		Assert.assertEquals(
				ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE,
				conf.isUseCertaintyToCalculateScore());
		Assert.assertEquals(
				TimeModelScorerConf.DEFAULT_MAX_RARE_TIMESTAMP_COUNT,
				conf.getMaxRareTimestampCount());
		Assert.assertEquals(
				TimeModelScorerConf.DEFAULT_MAX_NUM_OF_RARE_TIMESTAMPS,
				conf.getMaxNumOfRareTimestamps());
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_scorer_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, null, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, false, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_empty_scorer_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, "", getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, true, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_blank_scorer_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, "   ", getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, false, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_model_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(null),
				50, 100, true, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_empty_model_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(""),
				50, 100, false, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_blank_model_name() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject("   "),
				50, 100, true, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_min_num_of_samples_to_influence() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				0, 100, false, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_enough_num_of_samples_to_influence() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, -1, true, 36, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_max_rare_timestamp_count() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, false, -1, 42);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_max_num_of_rare_timestamps() throws IOException {
		String jsonString = getTimeModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				50, 100, true, 36, -10);

		getScorerConf(jsonString);
	}
}
