package fortscale.streaming.task;

import static fortscale.utils.ConversionUtils.*;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.task.TaskContext;

import fortscale.domain.system.ServersListConfiguration;
import fortscale.streaming.service.SpringService;
import net.minidev.json.JSONObject;

public class Sec4769EventsModelStreamTask extends EventsPrevalenceModelStreamTask {

	private static final String NAT_SRC_MACHINE = "nat_src_machine";
	private static final String FAILURE_AUGEMENTED ="aug_failure_code";
	
	private Pattern accountNamePattern;
	private Pattern destinationPattern;
	
	@Override
	public void init(Config config, TaskContext context) throws Exception {
		super.init(config, context);
		
		// get the account name and dc regex patterns from configuration
		ServersListConfiguration serversListConfiguration = SpringService.getInstance().resolve(ServersListConfiguration.class);
		
		String loginRegex = serversListConfiguration.getLoginAccountNameRegex();
		if (StringUtils.isNotBlank(loginRegex))
			accountNamePattern = Pattern.compile(loginRegex, Pattern.CASE_INSENSITIVE);
		
		String destinationRegex = serversListConfiguration.getLoginServiceRegex();
		if (StringUtils.isNotBlank(destinationRegex))
			destinationPattern = Pattern.compile(destinationRegex, Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	protected boolean acceptMessage(JSONObject message) {
				
		// filter events with account_name that match $account_regex parameter
		String account_name = convertToString(message.get("account_name"));
		if (accountNamePattern!=null && StringUtils.isNotBlank(account_name) && accountNamePattern.matcher(account_name).matches())
			return false;
		
		// filter events with service_name that match $dcRegex
		String service_name = convertToString(message.get("service_name"));
		if (destinationPattern!=null && StringUtils.isNotBlank(service_name) && destinationPattern.matcher(service_name).matches())
			return false;
		
		// filter events with no computer_name or no service_name or service_name that match the computer_name
		String machine_name = convertToString(message.get("machine_name"));
		if (machine_name==null || service_name==null || machine_name.equalsIgnoreCase(service_name))
			return false;
		
		// set field for source ip address only is it not nat, otherwise put don't care value in the event
		String normalized_src_machine = convertToString(message.get("normalized_src_machine")); 		
		Boolean is_nat = convertToBoolean(message.get("is_nat"));
		message.put(NAT_SRC_MACHINE, (Boolean.TRUE.equals(is_nat))? "" : normalized_src_machine);
		
		// the failure codes 0x12 and 0x22 are sent to the scoring algorithm with the event time value in order to get high score for them
		String failureCode = convertToString(message.get("failure_code"));
		message.put(FAILURE_AUGEMENTED, ("0x12".equals(failureCode) || "0x22".equals(failureCode))? System.currentTimeMillis() : "0x0");
		
		
		return true;
	}
}
