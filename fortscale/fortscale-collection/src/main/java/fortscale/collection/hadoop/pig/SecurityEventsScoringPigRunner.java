package fortscale.collection.hadoop.pig;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fortscale.global.configuration.ServersListConfiguration;

@Component("secevtScoringPigRunner")
public class SecurityEventsScoringPigRunner extends EventScoringPigRunner {

	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters, long earliestEventTime, long latestEventTime) throws IOException, NoPartitionExistException {
		scriptParameters.put("accountRegex", serversListConfiguration.getLoginAccountNameRegex());
		scriptParameters.put("dcRegex", serversListConfiguration.getLoginServiceRegex());
	}
	
}
