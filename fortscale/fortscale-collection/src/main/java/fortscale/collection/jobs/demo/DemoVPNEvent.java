package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoVPNEvent extends DemoGenericEvent {

	private Computer srcMachine;
	private String clientAddress;
	private String country;
	private String status;
	private String sourceIp;
	private String region;
	private String countryCode;
	private String city;
	private String ipUsage;
	private String isp;

	public DemoVPNEvent() {}

	public DemoVPNEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						String clientAddress, String country, String status, String sourceIp, String region,
						String countryCode, String city, String ipUsage, String isp) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.clientAddress = clientAddress;
		this.country = country;
		this.status = status;
		this.sourceIp = sourceIp;
		this.region = region;
		this.countryCode = countryCode;
		this.city = city;
		this.ipUsage = ipUsage;
		this.isp = isp;
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getCountry() {
		return country;
	}

	public String getStatus() {
		return status;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case COUNTRY: return country;
			case SOURCE: return srcMachine.getName();
			default: return null;
		}
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public String getRegion() {
		return region;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCity() {
		return city;
	}

	public String getIpUsage() {
		return ipUsage;
	}

	public String getIsp() {
		return isp;
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setIpUsage(String ipUsage) {
		this.ipUsage = ipUsage;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

}