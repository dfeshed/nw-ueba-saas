package fortscale.streaming.model.prevalance.field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.samza.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContinuousValuesFieldModelTest {

	private static int maxNumOfHistogramElements = 10;
	private static boolean scoreForLargeValues = true;
	private static boolean scoreForSmallValues = true;
	private static double a2 = 100.0/3;
	private static double a1 = 35.0/3;
	private static double largestPValue = 0.2;
	private static final String fieldName ="testField";
	
	private Config config;
	
	@Before
    public void setUp() throws Exception {

        config = mock(Config.class);
	}
	
	private ContinuousValuesFieldModel createContinuousValuesFieldModel(){
		ContinuousValuesFieldModel continuousValuesFieldModel = new ContinuousValuesFieldModel();
		when(config.getBoolean(String.format(ContinuousValuesFieldModel.SCORE_FOR_LARGE_VALUE_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().isScoreForLargeValues()))
				.thenReturn(scoreForLargeValues);
		when(config.getBoolean(String.format(ContinuousValuesFieldModel.SCORE_FOR_SMALL_VALUE_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().isScoreForSmallValues()))
				.thenReturn(scoreForSmallValues);
		when(config.getDouble(String.format(ContinuousValuesFieldModel.A1_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().getA1()))
				.thenReturn(a1);
		when(config.getDouble(String.format(ContinuousValuesFieldModel.A2_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().getA2()))
				.thenReturn(a2);
		when(config.getDouble(String.format(ContinuousValuesFieldModel.LARGEST_PVALUE_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().getLargestPValue()))
				.thenReturn(largestPValue);
		when(config.getInt(String.format(ContinuousValuesFieldModel.MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().getMaxNumOfHistogramElements()))
				.thenReturn(maxNumOfHistogramElements);
		continuousValuesFieldModel.init(fieldName, config);
		
		return continuousValuesFieldModel;
	}
	
	
	@Test
	public void uniformDistributionWithOneUpOutliersTest(){
		ContinuousValuesFieldModel continuousValuesFieldModel = createContinuousValuesFieldModel();
		double startVal = 1000000;
		for(int i=0; i < 1000; i++){
			double val = startVal+i;
			continuousValuesFieldModel.add(val,0);
		}
		
		//adding the outlier
		double outlierVal = startVal+1100;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(53.7,continuousValuesFieldModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1200;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(82.5,continuousValuesFieldModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1300;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(68.5,continuousValuesFieldModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1400;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(97.2,continuousValuesFieldModel.calculateScore(outlierVal),0.1);
		
		outlierVal = startVal+1500;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(97.0,continuousValuesFieldModel.calculateScore(outlierVal),0.1);
		
		Assert.assertEquals(73.5,continuousValuesFieldModel.calculateScore(startVal),0.1);
		
		Assert.assertEquals(0.0,continuousValuesFieldModel.calculateScore(startVal+500),0.0);		
		
	}
	
	@Test
	public void uniformDistributionWithOneDownOutlierTest(){
		ContinuousValuesFieldModel continuousValuesFieldModel = createContinuousValuesFieldModel();
		double startVal = 1000000;
		List<Double> vals = new ArrayList<>();
		for(int i=0; i < 1000; i++){
			double val = startVal+i;
			continuousValuesFieldModel.add(val,0);
			vals.add(val);
		}
		
		//adding the outlier
		double outlierVal = startVal/2;
		continuousValuesFieldModel.add(outlierVal,0);
		
		Assert.assertEquals(100,continuousValuesFieldModel.calculateScore(outlierVal),0.0);
		
		for(double val: vals){
			Assert.assertEquals(0.0,continuousValuesFieldModel.calculateScore(val),0.0);
		}
		
		
	}
}
