package fortscale.common.feature.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.feature.FeatureStringValue;
import org.junit.Assert;
import org.junit.Test;

public class ConstantValueFeatureAdjustorTest {

	private static final String JSON_TO_TEST = "{\"type\":\"const_val_feature_adjustor\",\"constantValue\":\"TEST_CONST_VAL\"}";
	private static final String CONSTANT_VALUE_FOR_JSON_TEST = "TEST_CONST_VAL";

	private FeatureAdjustor buildFeatureAdjustor(String constantValue){
		return new ConstantValueFeatureAdjustor(constantValue);
	}

	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(CONSTANT_VALUE_FOR_JSON_TEST);

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(featureAdjustor);

		//{"type":"const_val_feature_adjustor","constantValue":TEST_CONST_VAL}
		Assert.assertNotNull(json);

		Assert.assertEquals(JSON_TO_TEST, json);
	}

	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureAdjustor featureAdjustorActual = mapper.readValue(json, FeatureAdjustor.class);

		FeatureAdjustor featureAdjustorExpected = buildFeatureAdjustor(CONSTANT_VALUE_FOR_JSON_TEST);
		
		Assert.assertEquals(featureAdjustorExpected, featureAdjustorActual);
	}
	
	@Test
	public void testReturnedConstantValue() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor(CONSTANT_VALUE_FOR_JSON_TEST);
		
		FeatureStringValue adjustedFeature = (FeatureStringValue)featureAdjustor.adjust(new FeatureNumericValue(20D), null);
		
		Assert.assertEquals(CONSTANT_VALUE_FOR_JSON_TEST, adjustedFeature.toString());
	}
}
