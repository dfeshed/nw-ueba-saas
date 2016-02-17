package fortscale.collection.jobs.demo;

import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoSalesForceEvent extends DemoGenericEvent {

	private String clientAddress;
	private String actionType;
	private String city;
	private String country;
	private String status;
	private String loginType;
	private String browser;
	private String application;
	private String platform;

	public DemoSalesForceEvent() {}

	public DemoSalesForceEvent(User user, int score, DemoUtils.EventFailReason reason, String clientAddress,
							   String actionType, String city, String country, String status, String loginType, String browser,
							   String application, String platform) {
		super(user, score, reason);
		this.clientAddress = clientAddress;
		this.actionType = actionType;
		this.city = city;
		this.country = country;
		this.status = status;
		this.loginType = loginType;
		this.browser = browser;
		this.application = application;
		this.platform = platform;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getActionType() {
		return actionType;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getStatus() {
		return status;
	}

	public String getLoginType() {
		return loginType;
	}

	public String getBrowser() {
		return browser;
	}

	public String getApplication() {
		return application;
	}

	public String getPlatform() {
		return platform;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case COUNTRY: return country;
			case ACTION_TYPE: return actionType;
			case STATUS: return status;
			default: return null;
		}
	}

}