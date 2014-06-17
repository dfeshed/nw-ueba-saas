package fortscale.streaming.model;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fortscale.streaming.service.UserTopEvents;

public class UserTopEventsTest {
	@Test
	public void model_should_serialize_to_json() throws Exception {
		// build model	
		UserTopEvents userTopEvents = new UserTopEvents("vpn");
		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusDays(3);
		userTopEvents.updateEventScores(90, dateTime.getMillis());
		userTopEvents.updateEventScores(88, dateTime.getMillis());
		userTopEvents.updateEventScores(86, dateTime.minusDays(1).getMillis());
		userTopEvents.updateEventScores(84, dateTime.getMillis());
		userTopEvents.updateEventScores(82, dateTime.plusHours(40).getMillis());
		userTopEvents.updateEventScores(80, dateTime.plusHours(60).getMillis());
		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		String json = mapper.writeValueAsString(userTopEvents);

		Assert.assertNotNull(json);
		Assert.assertTrue(json.contains("\"score\":80.0"));
		Assert.assertTrue(json.contains("\"score\":82.0"));
		Assert.assertFalse(json.contains("\"score\":84.0"));
	}
	
	@Test
	public void model_should_deserialize_from_json() throws Exception {
		
        byte[] json = "{\"eventType\":\"vpn\",\"eventScores\":[{\"eventTime\":1402844660082,\"score\":90.0},{\"eventTime\":1402844660082,\"score\":88.0},{\"eventTime\":1402887860082,\"score\":80.0},{\"eventTime\":1402844660082,\"score\":84.0},{\"eventTime\":1402844660082,\"score\":82.0}]}".getBytes("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		UserTopEvents userTopEvents = mapper.readValue(json, UserTopEvents.class);
		
		Assert.assertNotNull(userTopEvents);
		Assert.assertEquals(80, userTopEvents.getEventScores().get(2).getScore(),0.01);
	}
}
