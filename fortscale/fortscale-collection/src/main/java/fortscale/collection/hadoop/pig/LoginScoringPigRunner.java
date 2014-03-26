package fortscale.collection.hadoop.pig;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.global.configuration.ServersListConfiguration;

@Component("loginScoringPigRunner")
public class LoginScoringPigRunner extends EventScoringPigRunner {
	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	private String pigLoginInputData;
	
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters, long earliestEventTime, long latestEventTime) throws IOException, NoPartitionExistException {
		scriptParameters.put("accountRegex", serversListConfiguration.getLoginAccountNameRegex());
		scriptParameters.put("dcRegex", serversListConfiguration.getLoginServiceRegex());
		scriptParameters.put("inputDataLogin", getInputDataParameter(pigLoginInputData, earliestEventTime, latestEventTime));
	}

	public void setPigLoginInputData(String pigLoginInputData) {
		this.pigLoginInputData = pigLoginInputData;
	}
}
