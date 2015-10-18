package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.service.ipresolving.utils.FsIpAddressContainer;
import fortscale.streaming.service.ipresolving.utils.FsIpAddressUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private Set<FsIpAddressContainer> reservedIpAddersses = null;

    public EventsIpResolvingService(IpToHostnameResolver resolver, List<EventResolvingConfig> configs) {
        checkNotNull(resolver);
        checkNotNull(configs);
        this.resolver = resolver;
        for (EventResolvingConfig config : configs) {
            this.configs.put(config.getInputTopic(), config);
        }

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
        if (StringUtils.isEmpty(ip) || timestamp == null)
            return event;

        if (!ipAddressShouldBeResolved(config, ip )) {
            return event;
        } else {

            // get the hostname from the resolver and put it into the event message
            String hostname = resolver.resolve(ip, timestamp, config.isRestrictToADName(), config.isShortName(), config.isRemoveLastDot());
            if (StringUtils.isNotEmpty(hostname)) {
                event.put(config.getHostFieldName(), hostname);
                if (config.isOverrideIPWithHostname()) {
                    event.put(config.getIpFieldName(), hostname);
                }
            } else {
                // check if we received an hostname to use externally - this could be in the case of
                // 4769 security event with 127.0.0.1 ip, in this case just normalize the name.
                // We do this after the ip resolving, to give a chance to resolve the ip to something correct in case
                // we will receive hostname field in the event in other cases than 127.0.0.1 for 4769, so it would be
                // better to override that hostname
                String eventHostname = convertToString(event.get(config.getHostFieldName()));
                if (StringUtils.isNotEmpty(eventHostname)) {
                    eventHostname = resolver.normalizeHostname(eventHostname, config.isRemoveLastDot(), config.isShortName());
                    event.put(config.getHostFieldName(), eventHostname);
                    if (config.isOverrideIPWithHostname()) {
                        event.put(config.getIpFieldName(), hostname);
                    }
                }
            }
        }
        return event;
    }

    private boolean ipAddressShouldBeResolved(EventResolvingConfig config, String sourceIpAddress){
        if (config.isResolveOnlyReservedIp()){
            //Resolve only IP addresses which match to reserved IP list.
            Set<FsIpAddressContainer> reservedFsIpAddressContainers = getResolvedIpAddresses(config.getReservedIpAddress());
            if (reservedFsIpAddressContainers.isEmpty()){
                return true; // Resolve all IP addresses
            } else {
                //Return true only if IP Address match to list
                for (FsIpAddressContainer reservedIpValue: reservedFsIpAddressContainers){
                    if (reservedIpValue.isMatch(sourceIpAddress)){
                        return true;
                    }
                }
                //Non of the ip ranges match. We don't want to resolve this IP
                return false;
            }
        } else {
            // Resolve all IP addresses
            return true;
        }
    }

    private Set<FsIpAddressContainer> getResolvedIpAddresses(String ipAddressesAsString){
        if (this.reservedIpAddersses == null) {
            Set<FsIpAddressContainer> tempReservedFsIpAddressContainers = new HashSet<>();
            if (StringUtils.isNotBlank(ipAddressesAsString)) {
                String[] ipAddresses = ipAddressesAsString.split(",");
                for (String ipAddress : ipAddresses){
//                    String IP_ADDRESS_REG_EXP = ".+\\..+\\..+\\..+";
//                    if (!ipAddress.matches(IP_ADDRESS_REG_EXP)){
//                        throw new InvalidValueException(ipAddress + " is not IN IP address format");
//                    }

                    ipAddress = ipAddress.trim();
                    tempReservedFsIpAddressContainers.add(FsIpAddressUtils.getIpAddressContainer(ipAddress));
                }

            }
            //If not exception was thrown - set the real ip address.
            this.reservedIpAddersses = tempReservedFsIpAddressContainers;
        }
        return this.reservedIpAddersses;
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

		return (config.isDropWhenFail() && StringUtils.isEmpty(convertToString(event.get(config.getHostFieldName()))));

	}



}
