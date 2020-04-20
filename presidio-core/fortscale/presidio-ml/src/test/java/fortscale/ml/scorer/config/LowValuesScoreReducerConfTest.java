package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LowValuesScoreReducerConfTest {
	private static final String DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME = "myLowValuesScoreReducer";

	private static JSONObject getBaseScorerConfJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", RegexScorerConf.SCORER_TYPE);
		jsonObject.put("name", "myRegexScorer");
		jsonObject.put("regex", ".*");
		jsonObject.put("regex-field-name", "myRegexField");
		return jsonObject;
	}

	private static List<JSONObject> getReductionConfigJsons(String[] reducingFeatureNames, double... doubles) {
		int nextIndex = 0;
		List<JSONObject> reductionConfigs = new ArrayList<>();

		for (String reducingFeatureName : reducingFeatureNames) {
			JSONObject reductionConfig = new JSONObject();
			reductionConfig.put("reducingFeatureName", reducingFeatureName);
			reductionConfig.put("reducingFactor", doubles[nextIndex++]);
			reductionConfig.put("maxValueForFullyReduce", doubles[nextIndex++]);
			reductionConfig.put("minValueForNotReduce", doubles[nextIndex++]);
			reductionConfigs.add(reductionConfig);
		}

		return reductionConfigs;
	}

	private static String getLowValuesScoreReducerConfString(
			String name, JSONObject baseScorerConf, List<JSONObject> reductionConfigs) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", LowValuesScoreReducerConf.SCORER_TYPE);
		jsonObject.put("name", name);
		jsonObject.put("base-scorer", baseScorerConf);
		jsonObject.put("reduction-configs", reductionConfigs);
		return jsonObject.toJSONString();
	}

	private static IScorerConf getScorerConf(String jsonString) throws IOException {
		return new ObjectMapper().readValue(jsonString, IScorerConf.class);
	}

	@Test
	public void deserialize_json() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getBaseScorerConfJson(),
				getReductionConfigJsons(new String[]{"myReducingFeature"}, 0.8, 100.0, 500.0));

		IScorerConf scorerConf = getScorerConf(jsonString);
		Assert.assertNotNull(scorerConf);
		Assert.assertEquals(LowValuesScoreReducerConf.class, scorerConf.getClass());

		LowValuesScoreReducerConf lowValuesScoreReducerConf = (LowValuesScoreReducerConf)scorerConf;
		Assert.assertNotNull(lowValuesScoreReducerConf.getBaseScorerConf());
		Assert.assertEquals(RegexScorerConf.class, lowValuesScoreReducerConf.getBaseScorerConf().getClass());

		RegexScorerConf regexScorerConf = (RegexScorerConf)lowValuesScoreReducerConf.getBaseScorerConf();
		Assert.assertEquals(regexScorerConf.getRegexPattern().toString(), getBaseScorerConfJson().get("regex").toString());
		Assert.assertEquals("myRegexField", regexScorerConf.getRegexFieldName());

		List<ReductionConfiguration> reductionConfigs = lowValuesScoreReducerConf.getReductionConfigs();
		Assert.assertNotNull(reductionConfigs);
		Assert.assertEquals(1, reductionConfigs.size());
		ReductionConfiguration reductionConfig = reductionConfigs.get(0);
		Assert.assertEquals("myReducingFeature", reductionConfig.getReducingFeatureName());
		Assert.assertEquals(0.8, reductionConfig.getReducingFactor(), 0);
		Assert.assertEquals(100.0, reductionConfig.getMaxValueForFullyReduce(), 0);
		Assert.assertEquals(500.0, reductionConfig.getMinValueForNotReduce(), 0);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_name() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				"   ", getBaseScorerConfJson(),
				getReductionConfigJsons(new String[]{"myReducingFeature"}, 0.8, 100.0, 500.0));
		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_base_scorer_conf() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, null,
				getReductionConfigJsons(new String[]{"myReducingFeature"}, 0.8, 100.0, 500.0));
		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_null_reduction_configs() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getBaseScorerConfJson(), null);
		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_invalid_reducing_feature_name() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getBaseScorerConfJson(),
				getReductionConfigJsons(new String[]{"   "}, 0.8, 100.0, 500.0));
		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_too_small_reducing_factor() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getBaseScorerConfJson(),
				getReductionConfigJsons(new String[]{"myReducingFeature"}, -0.5, 100.0, 500.0));
		getScorerConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_with_too_large_reducing_factor() throws IOException {
		String jsonString = getLowValuesScoreReducerConfString(
				DEFAULT_LOW_VALUES_SCORE_REDUCER_NAME, getBaseScorerConfJson(),
				getReductionConfigJsons(new String[]{"myReducingFeature"}, 1.5, 100.0, 500.0));
		getScorerConf(jsonString);
	}
}
