package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class EventsIpResolvingServiceTest {

    private EventsIpResolvingService service;
	private EventsIpResolvingService service2;
    private TaskMonitoringHelper taskMonitoringHelper;
    private IpToHostnameResolver resolver;
    private final String RESERVED_IP_RANGES = "10.0.0.0 - 10.255.255.255, 192.168.0.0 - 192.168.255.255, 1.1.1.1";
    private Map<StreamingTaskDataSourceConfigKey, EventResolvingConfig> configs = new HashMap<>();;

    @Before
    public void setUp() {

        StatsService statsService = null;
        StreamingTaskDataSourceConfigKey dataSourceConfigKey;

        dataSourceConfigKey = new StreamingTaskDataSourceConfigKey("vpn", null);
		configs.put(dataSourceConfigKey, EventResolvingConfig.build("vpn", "state", "ip", "host", "output",
                false, false, false, false, "time", "partition", false, true, RESERVED_IP_RANGES, dataSourceConfigKey, statsService));

        dataSourceConfigKey = new StreamingTaskDataSourceConfigKey("vpn", null);
        configs.put(dataSourceConfigKey, EventResolvingConfig.build("vpn", "state", "ip", "host", "output",
                false, false, false, true, "time", "partition", false, false, "", dataSourceConfigKey, statsService));

		resolver = mock(IpToHostnameResolver.class);
        resolver.createMetrics();

        taskMonitoringHelper = mock(TaskMonitoringHelper.class);
        service = new EventsIpResolvingService(resolver, configs, taskMonitoringHelper);
		service2 = new EventsIpResolvingService(resolver, configs, taskMonitoringHelper);
	}


    @Test
    public void service_should_ignore_events_with_empty_ip_address() throws FilteredEventException{
        JSONObject event = new JSONObject();
        event.put("time", 3L);

        EventResolvingConfig eventResolvingConfig = new EventResolvingConfig();
        StreamingTaskDataSourceConfigKey dataSourceConfigKey = new StreamingTaskDataSourceConfigKey(
                "TEST-DATA-SOURCE", "TEST-LAST-STEP");
        eventResolvingConfig.createMetrics(null, dataSourceConfigKey);

        MutableBoolean wasResolved = new MutableBoolean();
        JSONObject output = service.enrichEvent(eventResolvingConfig, event, wasResolved);
        Assert.assertNull(output.get("host"));
        Assert.assertFalse(wasResolved.getValue());
    }

    @Test
    public void service_should_ignore_events_with_empty_timestamp() throws FilteredEventException{
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");

        MutableBoolean wasResolved = new MutableBoolean();
        JSONObject output = service.enrichEvent(configs.values().iterator().next(), event, wasResolved);
        Assert.assertNull(output.get("host"));
        Assert.assertFalse( wasResolved.getValue() );
    }

    @Test
    public void service_should_add_hostname_to_event() throws FilteredEventException{
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");
        event.put("time", 3L);
        when(resolver.resolve("1.1.1.1", 3L, false, false, false)).thenReturn("my-pc");

        MutableBoolean wasResolved = new MutableBoolean();

        JSONObject output = service.enrichEvent(configs.values().iterator().next(), event, wasResolved);
        Assert.assertEquals("my-pc", output.get("host"));
        Assert.assertTrue(wasResolved.getValue());

        verify(resolver, times(1)).resolve("1.1.1.1", 3L, false, false, false);
    }


    @Test
    public void service_should_add_hostname_to_event_with_ip_in_range() throws FilteredEventException{
        JSONObject event = new JSONObject();
        event.put("ip", "192.168.4.4"); //192.168.4.4 is in the range RESERVED_IP_RANGES, between 192.168.0.0 - 192.168.255.255
        event.put("time", 3L);
        when(resolver.resolve("192.168.4.4", 3L, false, false, false)).thenReturn("my-pc");

        MutableBoolean wasResolved = new MutableBoolean();
        JSONObject output = service.enrichEvent(configs.values().iterator().next(), event, wasResolved);
        Assert.assertEquals("my-pc", output.get("host"));
        Assert.assertTrue(wasResolved.getValue());

        verify(resolver, times(1)).resolve("192.168.4.4", 3L, false, false, false);
    }


    @Test
    public void service_should_ignore_single_ip_not_in_reserved_ip_ranges() throws FilteredEventException{
        JSONObject event = new JSONObject();
        event.put("ip", "2.2.2.2"); //2.2.2.2 not in RESERVED_IP_RANGES
        event.put("time", 3L);

        MutableBoolean wasResolved = new MutableBoolean();
        JSONObject output = service.enrichEvent(configs.values().iterator().next(), event, wasResolved);
        Assert.assertEquals("", output.get("host"));
        Assert.assertFalse(wasResolved.getValue());

    }



    @Test
    public void service_should_return_output_topic_according_to_input_topic() throws FilteredEventException{
        String actual = service.getOutputTopic(configs.keySet().iterator().next());
        Assert.assertEquals("output", actual);
    }

    @Test
    public void service_should_return_partition_field() throws FilteredEventException {
        JSONObject event = new JSONObject();
        event.put("ip", "1.1.1.1");
        event.put("time", 3L);
        event.put("partition", "part-A");

        Object actual = service.getPartitionKey(configs.keySet().iterator().next(), event);
        Assert.assertEquals("part-A", actual);
    }

	public void service_should_filter_events_that_cant_resolved()
	{
		JSONObject event = new JSONObject();
		event.put("hostname", null);
		boolean res = service.filterEventIfNeeded(configs.values().iterator().next(), event, null);
		Assert.assertTrue(res);


		res = service2.filterEventIfNeeded(configs.values().iterator().next(), event, null);
		Assert.assertTrue(res);

		event.put("hostname", "");


		res = service2.filterEventIfNeeded(configs.values().iterator().next(), event, null);
		Assert.assertTrue(res);



	}

    public void service_should_filter_events_that_cant_resolved2()
    {
        JSONObject event = new JSONObject();
        event.put("hostname", null);
        boolean res = service.filterEventIfNeeded(configs.values().iterator().next(), event, null);
        Assert.assertTrue(res);

        event.put("hostname", "");

        res = service2.filterEventIfNeeded(configs.values().iterator().next(), event, null);
        Assert.assertTrue(res);



    }

}
