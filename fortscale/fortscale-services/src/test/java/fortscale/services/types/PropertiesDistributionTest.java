package fortscale.services.types;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertiesDistributionTest {
	
	@Test
	public void class_should_be_serialized_to_json() throws Exception {
		
		PropertiesDistribution subject = new PropertiesDistribution("country");
		subject.incValueCount("Israel",  8);
		subject.incValueCount("Thailand", 2);
		subject.calculateValuesDistribution();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(subject);
		
		Assert.assertNotNull(json);
		String expected = "{\"propertyName\":\"country\",\"conclusive\":true,\"entries\":[{\"propertyValue\":\"Thailand\",\"count\":2,\"percantage\":0.2},{\"propertyValue\":\"Israel\",\"count\":8,\"percantage\":0.8}]}";
		Assert.assertEquals(expected, json);
	}

}
