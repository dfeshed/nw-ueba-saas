package fortscale.services;

import java.util.List;

public interface ServersListConfiguration {

	public List<String> getDomainControllers();
	public String getLoginServiceRegex();
	public String getLoginAccountNameRegex();
}
