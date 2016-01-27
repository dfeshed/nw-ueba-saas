package fortscale.common.feature.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.common.feature.FeatureNumericValue;
import org.junit.Assert;


import org.junit.Test;

public class InverseValueFeatureAdjustorTest {

	private static final String JSON_TO_TEST = "{\"type\":\"inv_val_feature_adjustor\",\"denominator\":0.1}";
	private static final double DENOMINATOR_FOR_JSON_TEST = 0.1;

	private FeatureAdjustor buildFeatureAdjustor(double denominator){
		return new InverseValueFeatureAdjustor(denominator);
	}

	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(featureAdjustor);

		//{"type":"inv_val_feature_adjustor","denominator":0.1}
		Assert.assertNotNull(json);

		Assert.assertEquals(JSON_TO_TEST, json);
	}

	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureAdjustor featureAdjustorActual = mapper.readValue(json, FeatureAdjustor.class);

		FeatureAdjustor featureAdjustorExpected = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);

		Assert.assertEquals(featureAdjustorExpected, featureAdjustorActual);
	}

	@Test
	public void testPositiveDenominator() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);

		FeatureNumericValue adjustedVal = (FeatureNumericValue)featureAdjustor.adjust(new FeatureNumericValue(4.9), null);

		Assert.assertEquals(0.2, adjustedVal.getValue().doubleValue(),0.0);
	}

	@Test
	public void testZeroFeatureValue() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(DENOMINATOR_FOR_JSON_TEST);

		FeatureNumericValue adjustedVal = (FeatureNumericValue)featureAdjustor.adjust(new FeatureNumericValue(0), null);

		Assert.assertEquals(10, adjustedVal.getValue().doubleValue(),0.0);
	}
	
	@Test 
	public void testZeroDenominator() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(-0.1);

		FeatureNumericValue adjustedVal = (FeatureNumericValue)featureAdjustor.adjust(new FeatureNumericValue(0.1), null);

		Assert.assertNull(adjustedVal);
	}
}
