package fortscale.domain.ad;

import java.util.Map;

import fortscale.domain.core.ComputerUsageClassifier;


public class UserMachine {

	private String username;
	private String hostname;
	private int logonCount;
	private long lastLogon;
	private Boolean isSensitive;
	private String operatingSystem;
	private Map<String, ComputerUsageClassifier> usageClassifiers;

	public UserMachine() {}
	
	public UserMachine(String username, String hostname, int logonCount, long lastLogon) {
		this.username = username;
		this.hostname = hostname;
		this.logonCount = logonCount;
		this.lastLogon = lastLogon;
	}
	
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public int getLogonCount() {
		return logonCount;
	}
	
	public void setLogonCount(int logonCount) {
		this.logonCount = logonCount;
	}

	public long getLastlogon() {
		return lastLogon;
	}

	public void setLastlogon(long lastLogon) {
		this.lastLogon = lastLogon;
	}

	public Boolean getIsSensitive() {
		return isSensitive;
	}

	public void setIsSensitive(Boolean isSensitive) {
		this.isSensitive = isSensitive;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
	public Map<String, ComputerUsageClassifier> getUsageClassifiers() {
		return usageClassifiers;
	}
	
	public void setUsageClassifiers(
			Map<String, ComputerUsageClassifier> usageClassifiers) {
		this.usageClassifiers = usageClassifiers;
	}

}
