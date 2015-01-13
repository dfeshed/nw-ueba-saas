package fortscale.streaming.service.ipresolving;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToBoolean;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

import fortscale.services.ipresolving.IpToHostnameResolver;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that receive and event from a specific input topic, resolve the required ip field in it and
 * sends the enriched event to the designated output topic.
 */
public class EventsIpResolvingService {

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
        if (config==null)
            return event;


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
        return (configs.containsKey(inputTopic))? configs.get(inputTopic).getOutputTopic() : null;
    }

}
