package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.ml.scorer.QuadPolyCalibration;
import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
import org.apache.samza.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContinuousDataDistributionTest {
	private static final String PREFIX = "prefix";
	private static final String FIELD_NAME = "fieldName";

	private static final double a1 = 35.0 / 3;
	private static final double a2 = 100.0 / 3;
	private static final double sensitivity = 1.0;

	private Config config;

	@Before
	public void setUp() throws Exception {
		config = mock(Config.class);
	}

	private ContinuousDataDistribution create(int numOfDistinctValues, double bucketSize) {
		ContinuousDataDistribution distribution = new ContinuousDataDistribution();

		when(config.getInt(eq(String.format("%s.%s.continuous.data.distribution.min.distinct.values", PREFIX, FIELD_NAME)), anyInt())).thenReturn(numOfDistinctValues);
		when(config.getInt(eq(String.format("%s.%s.continuous.data.distribution.max.distinct.values", PREFIX, FIELD_NAME)), anyInt())).thenReturn(numOfDistinctValues);
		when(config.getDouble(eq(String.format("%s.%s.continuous.data.distribution.min.bucket.size", PREFIX, FIELD_NAME)), anyDouble())).thenReturn(bucketSize);
		when(config.getDouble(eq(String.format("%s.%s.continuous.data.distribution.max.bucket.size", PREFIX, FIELD_NAME)), anyDouble())).thenReturn(bucketSize);

		distribution.init(PREFIX, FIELD_NAME, config);
		return distribution;
	}

	@Test
	public void uniform_distribution_with_one_up_outliers_test() {
		ContinuousDataDistribution distribution = create(10, 1.0);
		final int separator = ContinuousValuesModelScorerAlgorithm.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;

		double startVal = 1000000;
		for (int i = 0; i < 1000; i++) {
			double val = startVal + i;
			distribution.add(val, 0);
		}

		double outlierVal = startVal + 1100;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.04161, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1200;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.01820, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1300;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.01404, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1400;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.00540, distribution.calculateScore(outlierVal), 0.00001);

		outlierVal = startVal + 1500;
		distribution.add(outlierVal, 0);
		Assert.assertEquals(separator + 0.00186, distribution.calculateScore(outlierVal), 0.00001);
		Assert.assertEquals(-separator - 0.04095, distribution.calculateScore(startVal), 0.00001);
		Assert.assertEquals(-separator - 0.37778, distribution.calculateScore(startVal + 500), 0.00001);
	}

	@Test
	public void shouldScoreDecreasinglyAsSensitivityIncreases(){
		double sensitivities[] = new double[]{1, 2, 3};
		double expectedScores[] = new double[]{90, 81, 72};
		for (int test = 0; test < sensitivities.length; test++) {
			ContinuousDataDistribution distribution = create(10, 1.0);
			QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivities[test], true, true);
			distribution.add(0, 0);
			distribution.add(0.5, 0);
			distribution.add(-1, 0);
			for (int i = 0; i < 5; i++) {
				distribution.add(0.15, 0);
				distribution.add(-0.15, 0);
			}

			double score = calculateScore(distribution, 1, calibrationForContModel);
			Assert.assertEquals(expectedScores[test], score, 0.0);
		}
	}

	@Test
	public void continuous_test_1() throws Exception {
		runScenarioAndTestScores("src/test/model/continuousTest1.csv");
	}

	@Test
	public void continuous_test_2() throws Exception {
		runScenarioAndTestScores("src/test/model/continuousTest2.csv");
	}

	@Test
	public void large_scale_uniform_distribution_test() {
		ContinuousDataDistribution distribution = create(100, 1.0);
		double startVal = 1000000;
		List<Double> values = new ArrayList<>();

		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 100000; i++) {
				double value = startVal + i;
				distribution.add(value, 0);
				if (j == 0) {
					values.add(value);
				}
			}
		}

		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		double score = calculateScore(distribution, startVal, calibrationForContModel);
		Assert.assertEquals(23.0, score, 0.1);

		score = calculateScore(distribution, startVal + 50000, calibrationForContModel);
		Assert.assertEquals(0.0, score, 0.0);

		for (double val : values) {
			score = calculateScore(distribution, val, calibrationForContModel);
			Assert.assertEquals(23.0, score, 23.0);
		}
	}

	@Test
	public void large_scale_uniform_distribution_with_one_up_outlier_test() {
		ContinuousDataDistribution distribution = create(100, 1.0);
		double startVal = 1000000;
		List<Double> values = new ArrayList<>();

		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 100000; i++) {
				double value = startVal + i;
				distribution.add(value, 0);
				if (j == 0) {
					values.add(value);
				}
			}
		}

		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		double outlierVal = startVal * 2;
		distribution.add(outlierVal, 0);

		double score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(100, score, 0.0);

		score = calculateScore(distribution, startVal, calibrationForContModel);
		Assert.assertEquals(23.0, score, 0.1);

		score = calculateScore(distribution, startVal + 50000, calibrationForContModel);
		Assert.assertEquals(0.0, score, 0.0);

		for (double val : values) {
			score = calculateScore(distribution, val, calibrationForContModel);
			Assert.assertEquals(23.0, score, 23.0);
		}
	}

	@Test
	public void uniform_distribution_with_up_outliers_test() {
		ContinuousDataDistribution distribution = create(100, 1.0);
		double startVal = 1000000;
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			double value = startVal + i;
			distribution.add(value, 0);
			values.add(value);
		}

		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		double outlierVal = startVal + 1100;
		distribution.add(outlierVal, 0);

		double score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(53.0, score, 0.1);

		outlierVal = startVal + 1200;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(76.0, score, 0.1);

		outlierVal = startVal + 1300;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(90.0, score, 0.1);

		outlierVal = startVal + 1400;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(96.0, score, 0.1);

		outlierVal = startVal + 1500;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(99.0, score, 0.1);

		score = calculateScore(distribution, startVal, calibrationForContModel);
		Assert.assertEquals(24.0, score, 0.1);

		score = calculateScore(distribution, startVal + 500, calibrationForContModel);
		Assert.assertEquals(0.0, score, 0.0);

		for (double val : values) {
			score = calculateScore(distribution, val, calibrationForContModel);
			Assert.assertEquals(12.0, score, 12.0);
		}
	}

	@Test
	public void uniform_distribution_of_fraction_values_with_up_outliers_test() {
		ContinuousDataDistribution distribution = create(100, 0.001);
		double startVal = 1000000;
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			double value = startVal + i * 0.001;
			distribution.add(value, 0);
			values.add(value);
		}

		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		double outlierVal = startVal + 1.1;
		distribution.add(outlierVal, 0);

		double score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(53.0, score, 0.1);

		outlierVal = startVal + 1.2;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(76.0, score, 0.1);

		outlierVal = startVal + 1.3;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(90.0, score, 0.1);

		outlierVal = startVal + 1.4;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(96.0, score, 0.1);

		outlierVal = startVal + 1.5;
		distribution.add(outlierVal, 0);
		score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(99.0, score, 0.1);

		score = calculateScore(distribution, startVal, calibrationForContModel);
		Assert.assertEquals(24.0, score, 0.1);

		score = calculateScore(distribution, startVal + 0.5, calibrationForContModel);
		Assert.assertEquals(0.0, score, 0.0);

		for (double val : values) {
			score = calculateScore(distribution, val, calibrationForContModel);
			Assert.assertEquals(12.0, score, 12.0);
		}
	}

	@Test
	public void uniform_distribution_with_one_down_outlier_test() {
		ContinuousDataDistribution distribution = create(100, 1.0);
		double startVal = 1000000;
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			double value = startVal + i;
			distribution.add(value, 0);
			values.add(value);
		}

		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		double outlierVal = startVal / 2;
		distribution.add(outlierVal, 0);

		double score = calculateScore(distribution, outlierVal, calibrationForContModel);
		Assert.assertEquals(100, score, 0.0);

		for (double val : values) {
			score = calculateScore(distribution, val, calibrationForContModel);
			Assert.assertEquals(0.0, score, 0.0);
		}
	}

	@Test
	public void model_should_serialize_to_json() throws Exception {
		ContinuousDataDistribution distribution = create(2, 0.3);
		distribution.add(10.0, 0);
		distribution.add(20.0, 0);
		distribution.add(30.0, 0);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		String json = mapper.writeValueAsString(distribution);


		Assert.assertNotNull(json);
		String expected = "{\"@class\":\"fortscale.ml.model.prevalance.field.ContinuousDataDistribution\",\"bucketSize\":19.2,\"minDistinctValues\":2,\"maxDistinctValues\":2,\"minBucketSize\":0.3,\"maxBucketSize\":0.3,\"distribution\":{\"19.2\":1,\"38.4\":2},\"totalCount\":3,\"continuousDataModel\":{\"N\":3,\"mean\":32.0,\"sd\":9.050966799187808}}";
		JSONAssert.assertEquals(expected, json, false);
	}

	@Test
	public void model_should_deserialize_from_json() throws Exception {
		String classNameJson = "\"@class\":\"fortscale.ml.model.prevalance.field.ContinuousDataDistribution\"";
		String distributionJson = "\"distribution\":{\"38.4\":2,\"19.2\":1}";
		String modelJson = "\"continuousDataModel\":{\"type\":\"continuous-data-model\",\"N\":3,\"mean\":32.0,\"sd\":9.050966799187808}";
		String otherFieldsJson = "\"bucketSize\":19.2,\"minDistinctValues\":2,\"maxDistinctValues\":2,\"minBucketSize\":0.3,\"maxBucketSize\":0.3,\"totalCount\":3";
		byte[] json = String.format("{%s,%s,%s,%s}", classNameJson, otherFieldsJson, distributionJson, modelJson).getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		ContinuousDataDistribution distribution = mapper.readValue(json, ContinuousDataDistribution.class);

		Assert.assertNotNull(distribution);
		double score = calculateScore(distribution, 40.0);
		Assert.assertEquals(0, score, 0.01);
		score = calculateScore(distribution, 22.0);
		Assert.assertEquals(0, score, 0.01);
	}

	private double calculateScore(ContinuousDataDistribution model, double value, QuadPolyCalibration calibrationForContModel) {
		double modelScore = model.calculateScore(value);
		return calibrationForContModel.calibrateScore(modelScore);
	}

	private double calculateScore(ContinuousDataDistribution distribution, double value) {
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		return calculateScore(distribution, value, calibrationForContModel);
	}

	private void runScenarioAndTestScores(String filePath) throws Exception {
		File file = new File(filePath);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String line;

			Map<Long, Double> valueToScoreMap = new HashMap<>();
			ContinuousDataDistribution distribution = create(100, 1.0);

			while ((line = reader.readLine()) != null) {
				String valueAndScore[] = line.split(",");
				Long value = Long.valueOf(valueAndScore[0]);
				Double score = Double.valueOf(valueAndScore[1]);
				valueToScoreMap.put(value, score);
				distribution.add(value.doubleValue(), 0);
			}

			QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
			for (Long value : valueToScoreMap.keySet()) {
				double score = calculateScore(distribution, value.doubleValue(), calibrationForContModel);
				Assert.assertEquals(valueToScoreMap.get(value), score, 0.5);
			}
		} finally{
			if(reader != null){
				reader.close();
			}
		}
	}
}
