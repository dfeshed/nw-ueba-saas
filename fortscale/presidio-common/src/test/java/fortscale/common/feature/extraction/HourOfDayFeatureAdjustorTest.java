package fortscale.common.feature.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.FeatureNumericValue;
import org.junit.Assert;
import org.junit.Test;

public class HourOfDayFeatureAdjustorTest {
	private static final String JSON_TO_TEST = "{\"type\":\"hour_of_day_feature_adjustor\"}";

	private FeatureAdjustor buildFeatureAdjustor(){
		return new HourOfDayFeatureAdjustor();
	}

	@Test
	public void serialize_to_json() throws JsonProcessingException{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor();

		ObjectMapper mapper = new ObjectMapper();

		String json = mapper.writeValueAsString(featureAdjustor);

		Assert.assertNotNull(json);

		Assert.assertEquals(JSON_TO_TEST, json);
	}

	@Test
	public void deserialize_from_json() throws Exception{
		byte[] json = JSON_TO_TEST.getBytes("UTF-8");

		ObjectMapper mapper = new ObjectMapper();
		FeatureAdjustor featureAdjustorActual = mapper.readValue(json, FeatureAdjustor.class);

		FeatureAdjustor featureAdjustorExpected = buildFeatureAdjustor();

		Assert.assertEquals(featureAdjustorExpected, featureAdjustorActual);
	}

	@Test
	public void testClass() throws Exception{
		FeatureAdjustor featureAdjustor = buildFeatureAdjustor();

		//  7/18/2015, 12:05:53
		long timestamp = 1437210353;

		FeatureNumericValue adjustedTimestamp = (FeatureNumericValue)featureAdjustor.adjust(new FeatureNumericValue(timestamp), null);

		Assert.assertEquals(9, adjustedTimestamp.getValue().intValue());
	}
}
