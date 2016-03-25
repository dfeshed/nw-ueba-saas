package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LinearScoreReducerConfTest {
	public static final JSONObject defaultReducedScorerConfJsonObject;

	static {
		defaultReducedScorerConfJsonObject = new JSONObject();
		defaultReducedScorerConfJsonObject.put("type", ConstantRegexScorerConf.SCORER_TYPE);
		defaultReducedScorerConfJsonObject.put("name", "myConstantRegexScorer");
		defaultReducedScorerConfJsonObject.put("regex", "42");
		defaultReducedScorerConfJsonObject.put("regex-field-name", "myRegexField");
		defaultReducedScorerConfJsonObject.put("constant-score", 100);
	}

	@Test
	public void should_deserialize_linear_score_reducer_conf_from_json() throws IOException {
		String name = "myLinearScoreReducer";
		double reducingWeight = 0.5;

		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, name, defaultReducedScorerConfJsonObject, reducingWeight);
		IScorerConf scorerConf = getScorerConf(jsonObject);

		Assert.assertEquals(LinearScoreReducerConf.class, scorerConf.getClass());
		Assert.assertEquals(name, scorerConf.getName());
		Assert.assertEquals(getScorerConf(defaultReducedScorerConfJsonObject),
				((LinearScoreReducerConf)scorerConf).getReducedScorer());
		Assert.assertEquals(reducingWeight, ((LinearScoreReducerConf)scorerConf).getReducingWeight(), 0);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_name_is_null() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, null, defaultReducedScorerConfJsonObject, 0.5);
		getScorerConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_name_is_empty() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, "", defaultReducedScorerConfJsonObject, 0.5);
		getScorerConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_name_is_blank() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, "   ", defaultReducedScorerConfJsonObject, 0.5);
		getScorerConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_reduced_scorer_conf_is_null() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, "myLinearScoreReducer", null, 0.5);
		getScorerConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_reducing_weight_is_0_or_smaller() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, "myLinearScoreReducer", defaultReducedScorerConfJsonObject, -0.5);
		getScorerConf(jsonObject);
	}

	@Test(expected = JsonMappingException.class)
	public void should_fail_when_reducing_weight_is_1_or_larger() throws IOException {
		JSONObject jsonObject = getLinearScoreReducerConfJsonObject(
				LinearScoreReducerConf.SCORER_TYPE, "myLinearScoreReducer", defaultReducedScorerConfJsonObject, 1.5);
		getScorerConf(jsonObject);
	}

	public static JSONObject getLinearScoreReducerConfJsonObject(
			String type, String name, JSONObject reducedScorer, Double reducingWeight) {

		JSONObject jsonObject = new JSONObject();
		if (type != null) jsonObject.put("type", type);
		if (name != null) jsonObject.put("name", name);
		if (reducedScorer != null) jsonObject.put("reduced-scorer", reducedScorer);
		if (reducingWeight != null) jsonObject.put("reducing-weight", reducingWeight);
		return jsonObject;
	}

	public static IScorerConf getScorerConf(JSONObject jsonObject) throws IOException {
		return new ObjectMapper().readValue(jsonObject.toJSONString(), IScorerConf.class);
	}
}
