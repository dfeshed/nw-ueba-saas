package fortscale.streaming.model.prevalance.field;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ContinuousValuesModelTest {

	private static int maxNumOfHistogramElements = 100;
	private static boolean scoreForLargeValues = true;
	private static boolean scoreForSmallValues = true;
	private static double a2 = 100.0/3;
	private static double a1 = 35.0/3;
	private static double largestPValue = 0.2;
	
	private ContinuousValuesModel createContinuousValuesModel(double roundNumber){
		ContinuousValuesModel continuousValuesModel = new ContinuousValuesModel(roundNumber);
		continuousValuesModel.setA1(a1);
		continuousValuesModel.setA2(a2);
		continuousValuesModel.setLargestPValue(largestPValue);
		continuousValuesModel.setMaxNumOfHistogramElements(maxNumOfHistogramElements);
		continuousValuesModel.setScoreForLargeValues(scoreForLargeValues);
		continuousValuesModel.setScoreForSmallValues(scoreForSmallValues);
		
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
			Assert.assertEquals(valueToScoreMap.get(value), continuousValuesModel.calculateScore(value.doubleValue()),0.5);
		}
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
		
		Assert.assertEquals(21.3,continuousValuesModel.calculateScore(startVal),0.1);
		
		Assert.assertEquals(0.0,continuousValuesModel.calculateScore(startVal+50000),0.0);
		
		for(double val: vals){
			Assert.assertEquals(11.0,continuousValuesModel.calculateScore(val),11.0);
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
		
		Assert.assertEquals(21.3,continuousValuesModel.calculateScore(startVal),0.1);
		
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
		Assert.assertEquals(54.1,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1200;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(76.2,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1300;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(89.2,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1400;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(96.3,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1500;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(98.6,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		Assert.assertEquals(23.6,continuousValuesModel.calculateScore(startVal),0.1);
		
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
		Assert.assertEquals(54.1,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.2;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(76.2,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.3;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(89.2,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.4;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(96.3,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1.5;
		continuousValuesModel.add(outlierVal);
		Assert.assertEquals(98.6,continuousValuesModel.calculateScore(outlierVal),0.1);
		
		Assert.assertEquals(23.6,continuousValuesModel.calculateScore(startVal),0.1);
		
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
}
