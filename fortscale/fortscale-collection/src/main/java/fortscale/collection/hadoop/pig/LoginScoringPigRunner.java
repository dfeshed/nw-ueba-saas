package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.utils.config.ServersListConfiguration;

@Component("loginScoringPigRunner")
public class LoginScoringPigRunner extends EventScoringPigRunner {
	@Autowired
	private ServersListConfiguration serversListConfiguratione;
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){
		scriptParameters.put("accountRegex", serversListConfiguratione.getLoginAccountNameRegex());
		scriptParameters.put("dcRegex", serversListConfiguratione.getLoginServiceRegex());
	}
}
