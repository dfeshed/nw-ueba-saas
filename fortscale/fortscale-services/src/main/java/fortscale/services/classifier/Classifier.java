package fortscale.services.classifier;

import fortscale.domain.events.LogEventsEnum;
import fortscale.services.UserApplication;

public enum Classifier {
	vpn("vpn", "VPN", LogEventsEnum.vpn, UserApplication.vpn),
	auth("auth", "Logins", LogEventsEnum.login, UserApplication.active_directory),
	ssh("ssh", "SSH", LogEventsEnum.ssh, UserApplication.ssh);

	private String id;
	private String displayName;
	private LogEventsEnum logEventsEnum;
	private UserApplication userApplication;
	
	Classifier(String id, String displayName, LogEventsEnum logEventsEnum, UserApplication userApplication) {
		this.id = id;
		this.displayName = displayName;
		this.logEventsEnum = logEventsEnum;
		this.userApplication = userApplication;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public LogEventsEnum getLogEventsEnum() {
		return logEventsEnum;
	}

	public UserApplication getUserApplication() {
		return userApplication;
	}
}
