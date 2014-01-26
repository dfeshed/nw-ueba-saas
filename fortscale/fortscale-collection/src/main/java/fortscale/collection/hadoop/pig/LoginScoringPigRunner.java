package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.services.analyst.ConfigurationService;

@Component("loginScoringPigRunner")
public class LoginScoringPigRunner extends EventScoringPigRunner {
	@Autowired
	private ConfigurationService configurationService;
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){
		scriptParameters.put("accountRegex", configurationService.getLoginAccountNameRegex());
		scriptParameters.put("dcRegex", configurationService.getLoginServiceRegex());
	}
}
