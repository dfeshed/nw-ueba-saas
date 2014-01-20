package fortscale.collection.hadoop.pig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("vpnScoringPigRunner")
public class VpnScoringPigRunner extends EventScoringPigRunner {
	@Value("${vpn.pig.script.location:file:resources/pig/vpn.pig}")
	private Resource vpnPigScriptResource;
	
	@Override
	public Resource getPigScriptResource() {
		return vpnPigScriptResource;
	}

	@Override
	public String getInputDataFullPath() {
		return "/user/cloudera/data/vpn";
	}

	@Override
	public String getOutputDataFullPathPrefix() {
		return "/user/cloudera/processeddata/vpn/";
	}
}
