package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("loginScoringPigRunner")
public class LoginScoringPigRunner extends EventScoringPigRunner {
	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){
		scriptParameters.put("dcRegex", loginServiceNameRegex);
	}
}
