package fortscale.streaming.task;

import fortscale.services.ServersListConfiguration;
import fortscale.services.impl.ServersListConfigurationImpl;
import fortscale.services.impl.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.task.monitor.MonitorMessaages;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.task.TaskContext;

import java.util.regex.Pattern;

import static fortscale.utils.ConversionUtils.convertToBoolean;
import static fortscale.utils.ConversionUtils.convertToString;

public class Sec4769EventsFilterStreamTask extends EventsFilterStreamTask{
	private static final String NAT_SRC_MACHINE = "nat_src_machine";


	private static final String MONITOR_NAME = "4769-EventsFilterStreaming";

	private Pattern accountNamePattern;
	private Pattern destinationPattern;
	
	private boolean filterVpnPool;
	private Pattern vpnIpPool;

	@Override
	public void init(Config config, TaskContext context) throws Exception {
		super.init(config, context);
		
		// get the account name and dc regex patterns from configuration
		ServersListConfiguration serversListConfiguration = SpringService.getInstance().resolve(ServersListConfigurationImpl.class);
		
		String loginRegex = serversListConfiguration.getLoginAccountNameRegex();
		if (StringUtils.isNotBlank(loginRegex))
			accountNamePattern = Pattern.compile(loginRegex, Pattern.CASE_INSENSITIVE);
		
		String destinationRegex = serversListConfiguration.getLoginServiceRegex();
		if (StringUtils.isNotBlank(destinationRegex))
			destinationPattern = Pattern.compile(destinationRegex, Pattern.CASE_INSENSITIVE);


		// load vpn address pool filter settings
		filterVpnPool = config.getBoolean("fortscale.filter.vpnpool.enabled", false);
		if (filterVpnPool) {
			// load vpn address pool regex pattern
			String ips = convertToString(config.get("fortscale.filter.vpnpool.ip"));
			vpnIpPool = Pattern.compile(ips);
		}
	}
	
	@Override
	protected boolean acceptMessage(JSONObject message) {

		StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
		if (configKey == null){
			taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.CANNOT_EXTRACT_STATE_MESSAGE);
			++taskMetrics.cantExtractStateMessage;
			return false;
		}
		// filter events with account_name that match $account_regex parameter
		String account_name = convertToString(message.get("account_name"));
		if (accountNamePattern!=null && StringUtils.isNotBlank(account_name) && 
				(accountNamePattern.matcher(account_name).matches() ||  account_name.startsWith("krbtgt"))){
			taskMonitoringHelper.countNewFilteredEvents(configKey,MonitorMessaages.ACCOUNT_NAME_MATCH_TO_REGEX);
			++taskMetrics.accountNameMatchesLoginAccountRegex;
			return false;
		}

		// filter events with service_name that match $dcRegex
		String service_name = convertToString(message.get("service_name"));
		if (destinationPattern!=null && StringUtils.isNotBlank(service_name) && destinationPattern.matcher(service_name).matches()) {
			taskMonitoringHelper.countNewFilteredEvents(configKey,MonitorMessaages.SERVICE_NAME_MATCH_TO_REGEX);
			++taskMetrics.serviceNameMatchesLoginServiceRegex;
			return false;
		}
		
		// filter events with service_name that match the computer_name
		String machine_name = convertToString(message.get("machine_name"));
		if (StringUtils.isNotBlank(machine_name) && machine_name.equalsIgnoreCase(service_name)){
			taskMonitoringHelper.countNewFilteredEvents(configKey,MonitorMessaages.SERVICE_NAME_MATCH_COMPUTER_NAME);
			++taskMetrics.serviceNameMatchesComputerName;
			return false;
		}
		
		// set field for source ip address only is it not nat, otherwise put don't care value in the event
		String normalized_src_machine = convertToString(message.get("normalized_src_machine")); 		
		Boolean is_nat = convertToBoolean(message.get("is_nat"));
		message.put(NAT_SRC_MACHINE, (Boolean.TRUE.equals(is_nat))? "" : normalized_src_machine);	
				

		// omit resolved machines names in cases where the source ip is from a vpn address pool
		// and the machine name does not resembles the user account name
		if (filterVpnPool) {
			// check if the source machine is not empty and the client address belongs to the vpn pool
			String clientIp = convertToString(message.get("client_address"));
			if (StringUtils.isNotEmpty(machine_name) && vpnIpPool.matcher(clientIp).matches()) {
				// strip the user name and machine name from seperator chars
				String coreMachineName = machine_name.split("-")[0].toLowerCase();
				String coreUserName = account_name.split("@")[0].split("\\.")[0].toLowerCase();
				if (!coreMachineName.equals(coreUserName)) {
					// replace the machine name and normalized src machine with empty values
					message.put("machine_name", "");
					message.put("normalized_src_machine", "");
					message.put(NAT_SRC_MACHINE, "");
					message.put("is_nat", true);
					++taskMetrics.sourceIpInVpnAddressPool;
				}
			}
		}


        super.acceptMessage(message);
		
		return true;
	}


	@Override
	protected String getJobLabel(){
		return MONITOR_NAME;
	}
}
