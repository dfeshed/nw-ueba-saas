package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.ml.scorer.QuadPolyCalibration;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContinuousValuesModelTest {

	private static int maxNumOfHistogramElements = 100;
	private static double a2 = 100.0/3;
	private static double a1 = 35.0/3;
	private static double sensitivity = 1.0;
	
	private ContinuousValuesModel createContinuousValuesModel(double roundNumber){
		ContinuousValuesModel continuousValuesModel = new ContinuousValuesModel(roundNumber);
		continuousValuesModel.setMaxNumOfHistogramElements(maxNumOfHistogramElements);
		
		return continuousValuesModel;
	}
	
	private ContinuousValuesModel createContinuousValuesModel(){
		return createContinuousValuesModel(1.0);
	}
	
	@SuppressWarnings("resource")
	private void runScenarioAndTestScores(String filePath) throws Exception{
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel();
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		Map<Long, Double> valueToScoreMap = new HashMap<>();
		while((line = reader.readLine()) != null){
			String valueAndScore[] = line.split(",");
			Long value = Long.valueOf(valueAndScore[0]);
			Double score = Double.valueOf(valueAndScore[1]);
			valueToScoreMap.put(value, score);
			continuousValuesModel.add(value.doubleValue());
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		for(Long value: valueToScoreMap.keySet()){
			double score = calculateScore(continuousValuesModel, value.doubleValue(), calibrationForContModel);
			Assert.assertEquals(valueToScoreMap.get(value), score,0.5);
		}
	}
	private double calculateScore(ContinuousValuesModel continuousValuesModel, double value){
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		return calculateScore(continuousValuesModel, value, calibrationForContModel);
	}
	
	private double calculateScore(ContinuousValuesModel continuousValuesModel, double value, QuadPolyCalibration calibrationForContModel){
		double modelScore = continuousValuesModel.calculateScore(value);
		
		return calibrationForContModel.calibrateScore(modelScore);
	}
	
	@Test
	public void test1() throws Exception{
		runScenarioAndTestScores("src/test/model/continuousTest1.csv");
	}
	
	@Test
	public void test2() throws Exception{
		runScenarioAndTestScores("src/test/model/continuousTest2.csv");
	}
	
	@Test
	public void largeScaleUniformDistributionTest(){
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel();
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int j=0; j < 10; j++){
			for(int i=0; i < 100000; i++){
				double val = startVal+i;
				continuousValuesModel.add(val);
				if(j==0){
					vals.add(val);
				}
			}
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		double score = calculateScore(continuousValuesModel, startVal, calibrationForContModel);
		Assert.assertEquals(21.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal+50000, calibrationForContModel);
		Assert.assertEquals(0.0,score,0.0);
		
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, calibrationForContModel);
			Assert.assertEquals(11.0,score,11.0);
		}
	}
	
	@Test
	public void largeScaleUniformDistributionWithOneUpOutlierTest(){
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel();
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int j=0; j < 10; j++){
			for(int i=0; i < 100000; i++){
				double val = startVal+i;
				continuousValuesModel.add(val);
				if(j==0){
					vals.add(val);
				}
			}
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		//adding the outlier
		double outlierVal = startVal*2;
		continuousValuesModel.add(outlierVal);
		
		double score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(100,score,0.0);
		
		score = calculateScore(continuousValuesModel, startVal, calibrationForContModel);
		Assert.assertEquals(21.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal+50000, calibrationForContModel);
		Assert.assertEquals(0.0,score,0.0);
		
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, calibrationForContModel);
			Assert.assertEquals(11.0,score,11.0);
		}
		
		
	}
	
	@Test
	public void uniformDistributionWithUpOutliersTest(){
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel();
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int i=0; i < 1000; i++){
			double val = startVal+i;
			continuousValuesModel.add(val);
			vals.add(val);
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		//adding the outlier
		double outlierVal = startVal+1100;
		continuousValuesModel.add(outlierVal);
		double score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(54.0,score,0.1);
		
		outlierVal = startVal+1200;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(76.0,score,0.1);
		
		outlierVal = startVal+1300;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(89.0,score,0.1);
		
		outlierVal = startVal+1400;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(96.0,score,0.1);
		
		outlierVal = startVal+1500;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(99.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal, calibrationForContModel);
		Assert.assertEquals(24.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal+500, calibrationForContModel);
		Assert.assertEquals(0.0,score,0.0);
		
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, calibrationForContModel);
			Assert.assertEquals(12.0,score,12.0);
		}
		
		
	}
	
	@Test
	public void uniformDistributionOfFractionValuesWithUpOutliersTest(){
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel(0.001);
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int i=0; i < 1000; i++){
			double val = startVal+i*0.001;
			continuousValuesModel.add(val);
			vals.add(val);
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		//adding the outlier
		double outlierVal = startVal+1.1;
		continuousValuesModel.add(outlierVal);
		double score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(54.0,score,0.1);
		
		outlierVal = startVal+1.2;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(76.0,score,0.1);
		
		outlierVal = startVal+1.3;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(89.0,score,0.1);
		
		outlierVal = startVal+1.4;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(96.0,score,0.1);
		
		outlierVal = startVal+1.5;
		continuousValuesModel.add(outlierVal);
		score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(99.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal, calibrationForContModel);
		Assert.assertEquals(24.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal+0.5, calibrationForContModel);
		Assert.assertEquals(0.0,score,0.0);
		
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, calibrationForContModel);
			Assert.assertEquals(12.0,score,12.0);
		}
		
		
	}
	
	@Test
	public void uniformDistributionWithOneDownOutlierTest(){
		ContinuousValuesModel continuousValuesModel = createContinuousValuesModel();
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int i=0; i < 1000; i++){
			double val = startVal+i;
			continuousValuesModel.add(val);
			vals.add(val);
		}
		
		QuadPolyCalibration calibrationForContModel = new QuadPolyCalibration(a2, a1, sensitivity, true, true);
		
		//adding the outlier
		double outlierVal = startVal/2;
		continuousValuesModel.add(outlierVal);
		double score = calculateScore(continuousValuesModel, outlierVal, calibrationForContModel);
		Assert.assertEquals(100,score,0.0);
				
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, calibrationForContModel);
			Assert.assertEquals(0.0,score,0.0);
		}
		
		
	}
	
	@Test
	public void model_should_serialize_to_json() throws Exception {
		// build model	
		ContinuousValuesModel continuousValuesModel = new ContinuousValuesModel(0.3);
		continuousValuesModel.setMaxNumOfHistogramElements(2);
		continuousValuesModel.add(10.0);
		continuousValuesModel.add(20.0);
		continuousValuesModel.add(30.0);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		String json = mapper.writeValueAsString(continuousValuesModel);

		Assert.assertNotNull(json);
		String expected = "{\"roundNumber\":19.2,\"histogram\":{\"19.2\":1.0,\"38.4\":2.0},\"maxNumOfHistogramElements\":2,\"histogramAvg\":32.0,\"histogramStd\":9.050966799187808,\"N\":3}";
		JSONAssert.assertEquals(expected, json, false);
	}
	
	@Test
	public void model_should_deserialize_from_json() throws Exception {
		
        byte[] json = "{\"roundNumber\":19.2,\"histogram\":{\"38.4\":2.0,\"19.2\":1.0},\"maxNumOfHistogramElements\":2,\"histogramAvg\":32.0,\"histogramStd\":9.050966799187808,\"N\":3}".getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		ContinuousValuesModel continuousValuesModel = mapper.readValue(json, ContinuousValuesModel.class);
		
		Assert.assertNotNull(continuousValuesModel);
		double score = calculateScore(continuousValuesModel, 40.0);
		Assert.assertEquals(0, score,0.01);
		score = calculateScore(continuousValuesModel, 22.0);
		Assert.assertEquals(6.0, score,0.01);
	}
}
