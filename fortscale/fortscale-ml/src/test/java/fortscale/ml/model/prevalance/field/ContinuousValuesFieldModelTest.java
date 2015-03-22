package fortscale.ml.model.prevalance.field;

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
	private static final String fieldName ="testField";
	private static final String prefix = "fortscale.fields";
	
	private Config config;
	
	@Before
    public void setUp() throws Exception {

        config = mock(Config.class);
	}
	
	private ContinuousValuesFieldModel createContinuousValuesFieldModel(){
		ContinuousValuesFieldModel continuousValuesFieldModel = new ContinuousValuesFieldModel();
		
		when(config.getInt(String.format(ContinuousValuesFieldModel.MAX_NUM_OF_HISTOGRAM_ELEMENTS_CONFIG_FORMAT, prefix, fieldName), 
				continuousValuesFieldModel.getContinuousValuesModel().getMaxNumOfHistogramElements()))
				.thenReturn(maxNumOfHistogramElements);
		continuousValuesFieldModel.init(prefix,fieldName, config);
		
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
		Assert.assertEquals(ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.04567,continuousValuesFieldModel.calculateScore(outlierVal),0.00001);
		
		outlierVal = startVal+1200;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.0157,continuousValuesFieldModel.calculateScore(outlierVal),0.00001);
		
		outlierVal = startVal+1300;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.0295,continuousValuesFieldModel.calculateScore(outlierVal),0.00001);
		
		outlierVal = startVal+1400;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.002442,continuousValuesFieldModel.calculateScore(outlierVal),0.00001);
		
		outlierVal = startVal+1500;
		continuousValuesFieldModel.add(outlierVal,0);
		Assert.assertEquals(ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.00258,continuousValuesFieldModel.calculateScore(outlierVal),0.00001);
		
		Assert.assertEquals(-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY - 0.02439,continuousValuesFieldModel.calculateScore(startVal),0.00001);
		
		Assert.assertEquals(-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY - 0.3489,continuousValuesFieldModel.calculateScore(startVal+500),0.00001);		
		
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
		
		Assert.assertEquals(-ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY,continuousValuesFieldModel.calculateScore(outlierVal),0.0);
		
		for(double val: vals){
			double pvalue = continuousValuesFieldModel.calculateScore(val);
			Assert.assertTrue(String.format("assertion for value %s. the p value is : %s", val, pvalue), pvalue > ContinuousValuesModel.SEPARATOR_BETWEEN_SMALL_AND_LARGE_VALUE_DENSITY + 0.398);
		}
		
		
	}
}
