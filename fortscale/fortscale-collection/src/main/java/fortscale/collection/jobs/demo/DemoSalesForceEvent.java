package fortscale.collection.jobs.demo;

import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoSalesForceEvent extends DemoEvent {

	private String clientAddress;
	private String actionType;
	private String city;
	private String country;
	private String status;
	private String loginType;
	private String browser;
	private String application;
	private String platform;

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

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case COUNTRY: return country;
			case ACTION_TYPE: return actionType;
			case STATUS: return status;
			default: return null;
		}
	}

}