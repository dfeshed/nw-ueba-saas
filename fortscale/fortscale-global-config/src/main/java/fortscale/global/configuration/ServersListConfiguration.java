package fortscale.global.configuration;

import java.util.List;

public interface ServersListConfiguration {

	public List<String> getDCs();
	public String getLoginServiceRegex();
	public String getLoginAccountNameRegex();
}
