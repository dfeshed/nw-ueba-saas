package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.service.StreamingTaskConfigurationService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.ipresolving.utils.FsIpAddressContainer;
import fortscale.streaming.service.ipresolving.utils.FsIpAddressUtils;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a specific input topic, resolve the required ip field in it and
 * sends the enriched event to the designated output topic.
 */
public class EventsIpResolvingService extends StreamingTaskConfigurationService<EventResolvingConfig> {


    private IpToHostnameResolver resolver;
    private Set<FsIpAddressContainer> reservedIpAddersses = null;

    private TaskMonitoringHelper taskMonitoringHelper;

    public EventsIpResolvingService(IpToHostnameResolver resolver, Map<StreamingTaskDataSourceConfigKey, EventResolvingConfig> configurations, TaskMonitoringHelper taskMonitoringHelper) {
        super(configurations);
        this.resolver = resolver;
        this.taskMonitoringHelper = taskMonitoringHelper;
    }

    public JSONObject enrichEvent(EventResolvingConfig eventResolvingConfig, JSONObject event) {

        // Get metrics from configuration
        EventsIpResolvingServiceMetrics metrics = eventResolvingConfig.getMetrics();
        metrics.enrichEventsAttempts++;

        // get the ip address and timestamp fields from the event
        String ip = convertToString(event.get(eventResolvingConfig.getIpFieldName()));
        Long timestamp = convertToLong(event.get(eventResolvingConfig.getTimestampFieldName()));
        if (StringUtils.isEmpty(ip) || timestamp == null) {
            metrics.enrichedEventMissingFields++;
            return event;
        }

        metrics.enrichMessageEpoch = timestamp;

        if (!ipAddressShouldBeResolved(eventResolvingConfig, ip )) {
            metrics.enrichedEventShouldNotResolved++;
            return event;
        } else {

            // get the hostname from the resolver and put it into the event message
            String hostname = resolver.resolve(ip, timestamp, eventResolvingConfig.isRestrictToADName(), eventResolvingConfig.isShortName(), eventResolvingConfig.isRemoveLastDot());

            if (StringUtils.isNotEmpty(hostname)) {

                metrics.enrichedEventResolved++;

                event.put(eventResolvingConfig.getHostFieldName(), hostname);
                if (eventResolvingConfig.isOverrideIPWithHostname()) {

                    metrics.enrichedEventResolvedOverridden++;
                    event.put(eventResolvingConfig.getIpFieldName(), hostname);
                }

            } else {

                metrics.enrichedEventNotResolved++;

                // check if we received an hostname to use externally - this could be in the case of
                // 4769 security event with 127.0.0.1 ip, in this case just normalize the name.
                // We do this after the ip resolving, to give a chance to resolve the ip to something correct in case
                // we will receive hostname field in the event in other cases than 127.0.0.1 for 4769, so it would be
                // better to override that hostname
                String eventHostname = convertToString(event.get(eventResolvingConfig.getHostFieldName()));
                if (StringUtils.isNotEmpty(eventHostname)) {
                    metrics.enrichedEventResolveForHostname++;
                    eventHostname = resolver.normalizeHostname(eventHostname, eventResolvingConfig.isRemoveLastDot(), eventResolvingConfig.isShortName());
                    event.put(eventResolvingConfig.getHostFieldName(), eventHostname);
                    if (eventResolvingConfig.isOverrideIPWithHostname()) {
                        metrics.enrichedEventResolveForHostnameAndOverridden++;
                        event.put(eventResolvingConfig.getIpFieldName(), hostname);
                    }
                }
            }
        }
        return event;
    }

    private boolean ipAddressShouldBeResolved(EventResolvingConfig config, String sourceIpAddress){

        // Get metrics from configuration
        EventsIpResolvingServiceMetrics metrics = config.getMetrics();

        if (config.isResolveOnlyReservedIp()){
            //Resolve only IP addresses which match to reserved IP list.
            Set<FsIpAddressContainer> reservedFsIpAddressContainers = getResolvedIpAddresses(config.getReservedIpAddress());
            if (reservedFsIpAddressContainers.isEmpty()){
                return true; // Resolve all IP addresses
            } else {
                //Return true only if IP Address match to list
                for (FsIpAddressContainer reservedIpValue: reservedFsIpAddressContainers){
                    if (reservedIpValue.isMatch(sourceIpAddress)){
                        metrics.resolveReservedIpMatched++;
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
                    ipAddress = ipAddress.trim();
                    tempReservedFsIpAddressContainers.add(FsIpAddressUtils.getIpAddressContainer(ipAddress));
                }

            }
            //If not exception was thrown - set the real ip address.
            this.reservedIpAddersses = tempReservedFsIpAddressContainers;
        }
        return this.reservedIpAddersses;
    }

	/** Drop Event when resolving fail??
	 *
	 */
	public boolean filterEventIfNeeded(EventResolvingConfig eventResolvingConfig, JSONObject event, StreamingTaskDataSourceConfigKey key)
	{
        EventsIpResolvingServiceMetrics metrics = eventResolvingConfig.getMetrics();

        boolean shouldFilterEvent = (eventResolvingConfig.isDropWhenFail() && StringUtils.isEmpty(convertToString(event.get(eventResolvingConfig.getHostFieldName()))));

        if (shouldFilterEvent){

            metrics.shouldFilterEvent++;

            if (key!=null) {
                taskMonitoringHelper.countNewFilteredEvents(key, MonitorMessaages.HOST_IS_EMPTY_LABEL);
            } else { //Fallback handler - should not arrive here
                taskMonitoringHelper.countNewFilteredEvents(AbstractStreamTask.UNKNOW_CONFIG_KEY, MonitorMessaages.HOST_IS_EMPTY_LABEL);
            }

            return true;
        }

		return false;
	}


	public void removeIpFromCache(String ip)
	{
		this.resolver.removeIpFromComputerLoginResolverCache(ip);
	}

}
