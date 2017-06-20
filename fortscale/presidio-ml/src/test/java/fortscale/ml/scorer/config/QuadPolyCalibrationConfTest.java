package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class QuadPolyCalibrationConfTest {
	private static String getQuadPolyCalibrationConfString(
			Double a1, Double a2, Double sensitivity,
			Boolean isScoreForSmallValues, Boolean isScoreForLargeValues) {

		JSONObject jsonObject = new JSONObject();
		if (a1 != null) jsonObject.put("a1", a1);
		if (a2 != null) jsonObject.put("a2", a2);
		if (sensitivity != null) jsonObject.put("sensitivity", sensitivity);
		if (isScoreForSmallValues != null) jsonObject.put("is-score-for-small-values", isScoreForSmallValues);
		if (isScoreForLargeValues != null) jsonObject.put("is-score-for-large-values", isScoreForLargeValues);
		return jsonObject.toJSONString();
	}

	private static QuadPolyCalibrationConf getQuadPolyCalibrationConf(String jsonString) throws IOException {
		return new ObjectMapper().readValue(jsonString, QuadPolyCalibrationConf.class);
	}

	@Test
	public void deserialize_json() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(1.0, 2.0, 3.0, false, false);
		QuadPolyCalibrationConf conf = getQuadPolyCalibrationConf(jsonString);
		Assert.assertNotNull(conf);
		Assert.assertEquals(1.0, conf.getA1(), 0);
		Assert.assertEquals(2.0, conf.getA2(), 0);
		Assert.assertEquals(3.0, conf.getSensitivity(), 0);
		Assert.assertEquals(false, conf.isScoreForSmallValues());
		Assert.assertEquals(false, conf.isScoreForLargeValues());
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_without_a1() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(null, 2.0, 3.0, false, false);
		getQuadPolyCalibrationConf(jsonString);
	}

	@Test(expected = JsonMappingException.class)
	public void deserialize_json_without_a2() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(1.0, null, 3.0, false, false);
		getQuadPolyCalibrationConf(jsonString);
	}

	@Test
	public void deserialize_json_without_sensitivity() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(1.0, 2.0, null, false, false);
		QuadPolyCalibrationConf conf = getQuadPolyCalibrationConf(jsonString);
		Assert.assertNotNull(conf);
		Assert.assertEquals(1.0, conf.getA1(), 0);
		Assert.assertEquals(2.0, conf.getA2(), 0);
		Assert.assertEquals(QuadPolyCalibrationConf.DEFAULT_SENSITIVITY, conf.getSensitivity(), 0);
		Assert.assertEquals(false, conf.isScoreForSmallValues());
		Assert.assertEquals(false, conf.isScoreForLargeValues());
	}

	@Test
	public void deserialize_json_without_is_score_for_small_values() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(1.0, 2.0, 3.0, null, false);
		QuadPolyCalibrationConf conf = getQuadPolyCalibrationConf(jsonString);
		Assert.assertNotNull(conf);
		Assert.assertEquals(1.0, conf.getA1(), 0);
		Assert.assertEquals(2.0, conf.getA2(), 0);
		Assert.assertEquals(3.0, conf.getSensitivity(), 0);
		Assert.assertEquals(QuadPolyCalibrationConf.DEFAULT_IS_SCORE_FOR_SMALL_VALUES, conf.isScoreForSmallValues());
		Assert.assertEquals(false, conf.isScoreForLargeValues());
	}

	@Test
	public void deserialize_json_without_is_score_for_large_values() throws IOException {
		String jsonString = getQuadPolyCalibrationConfString(1.0, 2.0, 3.0, false, null);
		QuadPolyCalibrationConf conf = getQuadPolyCalibrationConf(jsonString);
		Assert.assertNotNull(conf);
		Assert.assertEquals(1.0, conf.getA1(), 0);
		Assert.assertEquals(2.0, conf.getA2(), 0);
		Assert.assertEquals(3.0, conf.getSensitivity(), 0);
		Assert.assertEquals(false, conf.isScoreForSmallValues());
		Assert.assertEquals(QuadPolyCalibrationConf.DEFAULT_IS_SCORE_FOR_LARGE_VALUES, conf.isScoreForLargeValues());
	}
}
