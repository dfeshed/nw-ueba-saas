package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.IpToHostnameResolver;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a specific input topic, resolve the required ip field in it and
 * sends the enriched event to the designated output topic.
 */
public class EventsIpResolvingService {

    public static Logger logger = LoggerFactory.getLogger(EventsIpResolvingService.class);

    private IpToHostnameResolver resolver;
    private Map<String, EventResolvingConfig> configs = new HashMap<>();


    public EventsIpResolvingService(IpToHostnameResolver resolver, List<EventResolvingConfig> configs) {
        checkNotNull(resolver);
        checkNotNull(configs);
        this.resolver = resolver;
        for (EventResolvingConfig config : configs)
            this.configs.put(config.getInputTopic(), config);
    }

    public JSONObject enrichEvent(String inputTopic, JSONObject event) {
        checkNotNull(inputTopic);
        checkNotNull(event);

        // get the configuration for the input topic, if not found skip this event
        EventResolvingConfig config = configs.get(inputTopic);
        if (config==null) {
            logger.error("received event from topic {} that does not appear in configuration", inputTopic);
            return event;
        }


        // get the ip address and timestamp fields from the event
        String ip = convertToString(event.get(config.getIpFieldName()));
        Long timestamp = convertToLong(event.get(config.getTimestampFieldName()));
        if (StringUtils.isEmpty(ip) || timestamp==null)
            return event;

        // get the hostname from the resolver and put it into the event message
        String hostname = resolver.resolve(ip, timestamp, config.isRestrictToADName(), config.isShortName(), config.isRemoveLastDot());
        event.put(config.getHostFieldName(), hostname);

        return event;
    }

    public String getOutputTopic(String inputTopic) {
        if (configs.containsKey(inputTopic))
            return configs.get(inputTopic).getOutputTopic();
        else
            throw new RuntimeException("received events from topic " + inputTopic + " that does not appear in configuration");
    }

    /** Get the partition key to use for outgoing message envelope for the given event */
    public Object getPartitionKey(String inputTopic, JSONObject event) {
        checkNotNull(inputTopic);
        checkNotNull(event);

        // get the configuration for the input topic, if not found return empty key
        EventResolvingConfig config = configs.get(inputTopic);
        if (config==null) {
            logger.error("received event from topic {} that does not appear in configuration", inputTopic);
            return null;
        }

        return event.get(config.getPartitionField()).toString();
    }

	/** Drop Event when resolving fail??
	 *
	 */
	public boolean dropEvent(String inputTopic, JSONObject event)
	{
		checkNotNull(inputTopic);
		checkNotNull(event);


		// get the configuration for the input topic, if not found skip this event
		EventResolvingConfig config = configs.get(inputTopic);

		return (config.isDropWhenFail() && (event.get(config.getHostFieldName()) == null || convertToString(event.get(config.getHostFieldName())).isEmpty()));

	}



}
