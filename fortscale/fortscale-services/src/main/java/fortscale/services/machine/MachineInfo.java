package fortscale.services.machine;

/**
 * MachineInfo structure used by the EndpointDetectionService to return 
 * information regarding a given host name
 */
public class MachineInfo {

	private String hostname;
	private Boolean isEndpoint;
	private Boolean isServer;
	
	public MachineInfo(String hostname, Boolean isEndpoint, Boolean isServer) {
		this.setHostname(hostname);
		this.setIsEndpoint(isEndpoint);
		this.setIsServer(isServer);
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Boolean isEndpoint() {
		return isEndpoint;
	}

	public void setIsEndpoint(Boolean isEndpoint) {
		this.isEndpoint = isEndpoint;
	}

	public Boolean isServer() {
		return isServer;
	}

	public void setIsServer(Boolean isServer) {
		this.isServer = isServer;
	}
	
}
