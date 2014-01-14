package fortscale.collection.hadoop.pig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("sshScoringPigRunner")
public class SshScoringPigRunner extends EventScoringPigRunner {
	@Value("${ssh.pig.script.location:file:resources/pig/ssh.pig}")
	private Resource sshPigScriptResource;
	
	@Override
	public Resource getPigScriptResource() {
		return sshPigScriptResource;
	}

	@Override
	public String getInputDataFullPath() {
		return "/user/cloudera/data/ssh";
	}

	@Override
	public String getOutputDataFullPathPrefix() {
		return "/user/cloudera/processeddata/sshscores/";
	}
}
