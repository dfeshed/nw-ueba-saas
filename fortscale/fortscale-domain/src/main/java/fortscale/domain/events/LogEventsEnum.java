package fortscale.domain.events;

public enum LogEventsEnum {
	login("login", "login"),
	ssh("ssh", "ssh"),
	vpn("vpn", "vpn"),
	amt("amt", "amt"),
	amtsession("amtsession", "amtsession");

	private String id;
	private String displayName;
	
	LogEventsEnum(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
}
