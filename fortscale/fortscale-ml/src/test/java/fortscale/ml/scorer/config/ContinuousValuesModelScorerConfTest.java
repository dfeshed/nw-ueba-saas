package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ContinuousValuesModelScorerConfTest {
	private static final String SCORER_TYPE = ContinuousValuesModelScorerConf.SCORER_TYPE;
	private static final String DEFAULT_SCORER_NAME = "myScorer";
	private static final String DEFAULT_MODEL_NAME = "myModel";
	private static final int DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE = 50;
	private static final int DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE = 100;
	private static final JSONObject defaultQuadPolyCalibrationConf;

	static {
		defaultQuadPolyCalibrationConf = new JSONObject();
		defaultQuadPolyCalibrationConf.put("a1", 1.0);
		defaultQuadPolyCalibrationConf.put("a2", 2.0);
	}

	private static JSONObject getModelInfoJsonObject(String modelName) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", modelName);
		return jsonObject;
	}

	private static String getContinuousValuesModelScorerConfString(
			String scorerType, String name, JSONObject modelInfoJsonObject,
			Integer minNumOfSamplesToInfluence, Integer enoughNumOfSamplesToInfluence,
			Boolean isUseCertaintyToCalculateScore, JSONObject quadPolyCalibrationConf) {

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
		jsonObject.put("quad-poly-calibration-conf", quadPolyCalibrationConf);
		return jsonObject.toJSONString();
	}

	private static IScorerConf getScorerConf(String jsonString) throws IOException {
		return new ObjectMapper().readValue(jsonString, IScorerConf.class);
	}

	@Test
	public void deserialize_json() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				true, defaultQuadPolyCalibrationConf);

		IScorerConf scorerConf = getScorerConf(jsonString);
		Assert.assertNotNull(scorerConf);
		Assert.assertEquals(ContinuousValuesModelScorerConf.class, scorerConf.getClass());

		ContinuousValuesModelScorerConf conf = (ContinuousValuesModelScorerConf)scorerConf;
		Assert.assertEquals(DEFAULT_SCORER_NAME, conf.getName());
		Assert.assertEquals(DEFAULT_MODEL_NAME, conf.getModelInfo().getModelName());
		Assert.assertEquals(DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, conf.getMinNumOfSamplesToInfluence());
		Assert.assertEquals(DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE, conf.getEnoughNumOfSamplesToInfluence());
		Assert.assertEquals(true, conf.isUseCertaintyToCalculateScore());
	}

	@Test
	public void deserialize_json_without_non_mandatory_fields() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				null, null, null, defaultQuadPolyCalibrationConf);

		IScorerConf scorerConf = getScorerConf(jsonString);
		Assert.assertNotNull(scorerConf);
		Assert.assertEquals(ContinuousValuesModelScorerConf.class, scorerConf.getClass());

		ContinuousValuesModelScorerConf conf = (ContinuousValuesModelScorerConf)scorerConf;
		Assert.assertEquals(DEFAULT_SCORER_NAME, conf.getName());
		Assert.assertEquals(DEFAULT_MODEL_NAME, conf.getModelInfo().getModelName());
		Assert.assertEquals(
				ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE,
				conf.getMinNumOfSamplesToInfluence());
		Assert.assertEquals(
				ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE,
				conf.getEnoughNumOfSamplesToInfluence());
		Assert.assertEquals(
				ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEAFEST_VALUE,
				conf.isUseCertaintyToCalculateScore());
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_scorer_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, null, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				false, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_empty_scorer_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, "", getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				true, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_blank_scorer_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, "   ", getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				false, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_model_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(null),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				true, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_empty_model_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(""),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				false, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_blank_model_name() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject("   "),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				true, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_min_num_of_samples_to_influence() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				0, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				false, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_enough_num_of_samples_to_influence() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, -1,
				true, defaultQuadPolyCalibrationConf);

		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_quad_poly_calibration_conf() throws IOException {
		String jsonString = getContinuousValuesModelScorerConfString(
				SCORER_TYPE, DEFAULT_SCORER_NAME, getModelInfoJsonObject(DEFAULT_MODEL_NAME),
				DEFAULT_MIN_NUM_OF_SAMPLES_TO_INFLUENCE, DEFAULT_ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE,
				false, null);

		getScorerConf(jsonString);
	}
}
