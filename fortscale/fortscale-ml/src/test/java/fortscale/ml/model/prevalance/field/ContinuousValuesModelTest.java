package fortscale.ml.model.prevalance.field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ContinuousValuesModelTest {

	private static int maxNumOfHistogramElements = 100;
	private static double a2 = 100.0/3;
	private static double a1 = 35.0/3;
	private static double largestPValue = 0.2;
	
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
		String line = null;
		Map<Long, Double> valueToScoreMap = new HashMap<>();
		while((line = reader.readLine()) != null){
			String valueAndScore[] = line.split(",");
			Long value = Long.valueOf(valueAndScore[0]);
			Double score = Double.valueOf(valueAndScore[1]);
			valueToScoreMap.put(value, score);
			continuousValuesModel.add(value.doubleValue());
		}
		
		for(Long value: valueToScoreMap.keySet()){
			double score = calculateScore(continuousValuesModel, value.doubleValue(), true, true);
			Assert.assertEquals(valueToScoreMap.get(value), score,0.5);
		}
	}
	
	private double calculateScore(ContinuousValuesModel continuousValuesModel, double value, boolean isScoreForLargeValues, boolean isScoreForSmallValues){
		double score = 0;

		double val = continuousValuesModel.calculateScore(value);
		double p = Math.abs(val) - ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY;
		
		if(p < largestPValue && 
				((val > 0 && isScoreForLargeValues) || (val < 0 && isScoreForSmallValues)) ){
			score = Math.max(a2*Math.pow(p, 2) - a1*p + 1, 0);
			score = Math.round(score*100);
		}
		
		return score;
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
		
		double score = calculateScore(continuousValuesModel, startVal, true, true);
		Assert.assertEquals(21.0,score,0.1);
		
		score = calculateScore(continuousValuesModel, startVal+50000, true, true);
		Assert.assertEquals(0.0,score,0.0);
		
		for(double val: vals){
			score = calculateScore(continuousValuesModel, val, true, true);
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
		
		//adding the outlier
		double outlierVal = startVal*2;
		continuousValuesModel.add(outlierVal);
		
		Assert.assertEquals(100,continuousValuesModel.calculateScore(outlierVal),0.0);
		
		Assert.assertEquals(21.0,continuousValuesModel.calculateScore(startVal),0.1);
		
		Assert.assertEquals(0.0,continuousValuesModel.calculateScore(startVal+50000),0.0);
		
		for(double val: vals){
			Assert.assertEquals(11.0,continuousValuesModel.calculateScore(val),11.0);
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
		
		//adding the outlier
		double outlierVal = startVal+1100;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(54.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1200;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(76.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1300;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(89.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1400;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(96.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1500;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(99.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		Assert.assertEquals(24.0,continuousValuesModel.calculateScore(startVal),0.1);
		
		Assert.assertEquals(0.0,continuousValuesModel.calculateScore(startVal+500),0.0);
		
		for(double val: vals){
			Assert.assertEquals(12.0,continuousValuesModel.calculateScore(val),12.0);
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
		
		//adding the outlier
		double outlierVal = startVal+1.1;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(54.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.2;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(76.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.3;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(89.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.4;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(96.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.5;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(99.0,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		Assert.assertEquals(24.0,continuousValuesModel.calculateScore(startVal),0.1);
		
		Assert.assertEquals(0.0,continuousValuesModel.calculateScore(startVal+0.5),0.0);
		
		for(double val: vals){
			Assert.assertEquals(12.0,continuousValuesModel.calculateScore(val),12.0);
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
		
		//adding the outlier
		double outlierVal = startVal/2;
		continuousValuesModel.add(outlierVal);
		
		Assert.assertEquals(100,continuousValuesModel.calculateScore(outlierVal),0.0);
				
		for(double val: vals){
			Assert.assertEquals(0.0,continuousValuesModel.calculateScore(val),0.0);
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
		Assert.assertTrue(json.contains("\"histogram\":{\"38.4\":2.0,\"19.2\":1.0}"));
		Assert.assertTrue(json.contains("\"histogramAvg\":32.0"));
		Assert.assertFalse(json.contains("\"histogramStd\":050966799187808"));
	}
	
	@Test
	public void model_should_deserialize_from_json() throws Exception {
		
        byte[] json = "{\"roundNumber\":19.2,\"histogram\":{\"38.4\":2.0,\"19.2\":1.0},\"maxNumOfHistogramElements\":2,\"histogramAvg\":32.0,\"histogramStd\":9.050966799187808,\"N\":3,\"scoreForLargeValues\":true,\"scoreForSmallValues\":true,\"a2\":33.333333333333336,\"a1\":11.666666666666666,\"largestPValue\":0.2}".getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		ContinuousValuesModel continuousValuesModel = mapper.readValue(json, ContinuousValuesModel.class);
		
		Assert.assertNotNull(continuousValuesModel);
		Assert.assertEquals(0, continuousValuesModel.calculateScore(40.0),0.01);
		Assert.assertEquals(6.0, continuousValuesModel.calculateScore(22.0),0.01);
	}
}
