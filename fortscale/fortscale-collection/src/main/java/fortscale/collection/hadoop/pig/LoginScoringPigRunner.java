package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("loginScoringPigRunner")
public class LoginScoringPigRunner extends EventScoringPigRunner {
	@Value("${login.pig.script.location:file:resources/pig/authentication.pig}")
	private Resource loginPigScriptResource;
	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;
	
	@Override
	public Resource getPigScriptResource() {
		return loginPigScriptResource;
	}

	@Override
	public String getInputDataFullPath() {
		return "/user/cloudera/data/wmi4769";
	}

	@Override
	public String getOutputDataFullPathPrefix() {
		return "/user/cloudera/processeddata/authentication/";
	}
	
	@Override
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){
		scriptParameters.put("userToCompLocation", "/user/cloudera/processeddata/usertocomputer/history/");
		scriptParameters.put("dcRegex", loginServiceNameRegex);
	}
}
