package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.IpToHostnameResolver;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class EventsIpResolvingServiceTest {

    private EventsIpResolvingService service;
	private EventsIpResolvingService service2;
    private IpToHostnameResolver resolver;

    @Before
    public void setUp() {
		List<EventResolvingConfig> configs = new LinkedList<>();
		configs.add(EventResolvingConfig.build("input", "ip", "host", "output", false, false, false, false, "time", "partition", false));

		List<EventResolvingConfig> configs2 = new LinkedList<>();
		configs2.add(EventResolvingConfig.build("input", "ip", "host", "output", false, false, false, true, "time", "partition", false));

		resolver = mock(IpToHostnameResolver.class);
		service = new EventsIpResolvingService(resolver, configs);
		service2 = new EventsIpResolvingService(resolver, configs2);
	}


    @Test
    public void service_should_ignore_events_with_empty_ip_address() {
        JSONObject event = new JSONObject();
        event.put("time", 3L);

        JSONObject output = service.enrichEvent("input", event);
        Assert.assertNull(output.get("host"));
    }

    @Test
    public void service_should_ignore_events_with_empty_timestamp() {
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");

        JSONObject output = service.enrichEvent("input", event);
        Assert.assertNull(output.get("host"));
    }


    @Test
    public void service_should_ignore_and_not_touch_events_with_unknown_topics() {
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");
        event.put("time", 3L);
        when(resolver.resolve("1.1.1.1", 3L, false, false, false)).thenReturn("my-pc");

        JSONObject output = service.enrichEvent("unknown-topic", event);
        Assert.assertNull(output.get("host"));
    }

    @Test
    public void service_should_add_hostname_to_event(){
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");
        event.put("time", 3L);
        when(resolver.resolve("1.1.1.1", 3L, false, false, false)).thenReturn("my-pc");

        JSONObject output = service.enrichEvent("input", event);
        Assert.assertEquals("my-pc", output.get("host"));

        verify(resolver, times(1)).resolve("1.1.1.1", 3L, false, false, false);
    }

    @Test
    public void service_should_return_output_topic_according_to_input_topic() {
        String actual = service.getOutputTopic("input");
        Assert.assertEquals("output", actual);
    }

    @Test(expected =  Exception.class)
    public void service_should_fail_output_topic_in_case_of_unknown_input_topic() {
        String actual = service.getOutputTopic("unknown");
    }


    @Test
    public void service_should_return_partition_field() {
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");
        event.put("time", 3L);
        event.put("partition", "part-A");

        Object actual = service.getPartitionKey("input", event);
        Assert.assertEquals("part-A", actual);
    }

	@Test
	public void service_should_drop_events_that_cant_resolved()
	{
		JSONObject event = new JSONObject();
		event.put("hostname", null);
		boolean res = service.dropEvent("input",event);
		Assert.assertTrue(!res);


		res = service2.dropEvent("input",event);
		Assert.assertTrue(res);

		event.put("hostname", "");


		res = service2.dropEvent("input",event);
		Assert.assertTrue(res);



	}

}
