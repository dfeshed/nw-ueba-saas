package fortscale.streaming.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.serialization.AmtSessionSerdeFactory;
import org.apache.samza.serializers.Serde;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class AmtSessionTest {
	@Test
	public void testCreationFromJson() {
		// Create from JSON
		try {
			byte[] json = "{\"username\":\"frank65\"}".getBytes("UTF-8");
			ObjectMapper mapper = new ObjectMapper();
			AmtSession amtSession = mapper.readValue(json, AmtSession.class);
			Assert.assertNotNull(amtSession);
			assertEquals("frank65", amtSession.getUsername());
		} catch (UnsupportedEncodingException e) {
			Assert.fail("Cannot start test " + e.getMessage());
		} catch (IOException e) {
			Assert.fail("Cannot create AmtSession from JSON" + e.getMessage());
		}
	}

	@Test
	public void verifySerializeToStore() {
		// Create serializer
		AmtSessionSerdeFactory factory = new AmtSessionSerdeFactory();
		Serde<AmtSession> serde = factory.getSerde(null, null);

		// Create AMT session instance
		AmtSession session = new AmtSession("frank", "frank@fortscale.com");
		session.addYid("yid1", 100);
		session.addYid("yid1", 101);
		session.addYid("yid2", 200);

		byte[] actual = serde.toBytes(session);
		Assert.assertNotNull(actual);

		AmtSession deserialized = serde.fromBytes(actual);
		Assert.assertNotNull(deserialized);
	}

	@Test
	public void testCalculateAvgTimeOnYid() {
		AmtSession session = new AmtSession("testAvgYIDDuration", "testAvgYIDDuration@fortscale.com");
		// Add YID with 1 action code and a duration of 1 second
		session.addYid("yid01", 1420070400);
		// Add YID with 2 action codes and a duration of 59 seconds
		session.addYid("yid02", 1420074000);
		session.addYid("yid02", 1420074059);

		session.closeSession();

		double expectedAvgYidDuration = 0.5; // (1 + 59) / 2 = 30 seconds = 0.5 minutes
		double actualAvgYidDuration = session.getAverageTimeInYid();
		assertEquals(expectedAvgYidDuration, actualAvgYidDuration, 0);
	}

	@Test
	public void incActionTypeCount_should_start_non_existing_action_type_count_with_1() {
		AmtSession session = new AmtSession("testAvgYIDDuration", "testAvgYIDDuration@fortscale.com");
		session.incActionTypeCount(AmtSession.ActionType.Failed);
		assertEquals(session.getActionTypeCount(AmtSession.ActionType.Failed), 1);
	}

	@Test
	public void incActionTypeCount_should_increase_existing_action_type_by_1() {
		AmtSession session = new AmtSession("testAvgYIDDuration", "testAvgYIDDuration@fortscale.com");
		session.incActionTypeCount(AmtSession.ActionType.Failed);
		session.incActionTypeCount(AmtSession.ActionType.Failed);
		assertEquals(session.getActionTypeCount(AmtSession.ActionType.Failed), 2);
	}

	@Test
	public void getActionTypeCount_should_return_0_for_non_existing_action_type() {
		AmtSession session = new AmtSession("testAvgYIDDuration", "testAvgYIDDuration@fortscale.com");
		assertEquals(session.getActionTypeCount(AmtSession.ActionType.Failed), 0);
	}

	@Test
	public void testRemoveOutstandingYids() {
		AmtSession amtSession = new AmtSession("testRemoveOutstandingYids", "testRemoveOutstandingYids@fortscale.com");
		long timestamp = 1420074000;

		amtSession.addYid("yid1", timestamp);
		timestamp += 5;
		amtSession.addYid("yid2", timestamp);
		timestamp += 5;
		amtSession.addYid("yid3", timestamp);
		timestamp += 5;
		amtSession.addYid("yid1", timestamp);
		timestamp += 25;
		amtSession.addYid("yid2", timestamp);
		timestamp += 10;
		amtSession.addYid("yid3", timestamp);

		// yid1 duration = 15 seconds
		// yid2 duration = 35 seconds
		// yid3 duration = 40 seconds
		// average duration = 30 seconds = 0.5 minutes

		// Force session timeout for existing YIDs
		timestamp += AmtSession.staleYidSessionTimeoutMillis;
		amtSession.addYid("yid4", timestamp);

		assertEquals(3, amtSession.getYidCount());
		assertEquals(new Double(0.5), (Double)amtSession.getAverageTimeInYid());

		timestamp += 10;
		amtSession.addYid("yid5", timestamp);
		timestamp += 20;
		amtSession.addYid("yid6", timestamp);
		timestamp += 30;
		amtSession.addYid("yid4", timestamp);
		timestamp += 10;
		amtSession.addYid("yid5", timestamp);
		timestamp += 50;
		amtSession.addYid("yid6", timestamp);

		// yid4 duration = 60 seconds
		// yid5 duration = 60 seconds
		// yid6 duration = 90 seconds
		// new total average = (15 + 35 + 40 + 60 + 60 + 90) / 6 = 50 seconds = 0.833 minutes

		// Force session timeout for existing YIDs
		timestamp += AmtSession.staleYidSessionTimeoutMillis;
		amtSession.addYid("yid0", timestamp); // duration = 1 second

		assertEquals(6, amtSession.getYidCount());
		assertEquals(new Double(0.83), (Double)amtSession.getAverageTimeInYid());

		amtSession.closeSession();
		// final average = (15 + 35 + 40 + 60 + 60 + 90 + 1) / 7 = 43 seconds = 0.716 minutes
		assertEquals(7, amtSession.getYidCount());
		assertEquals(new Double(0.72), (Double)amtSession.getAverageTimeInYid());
	}
}
