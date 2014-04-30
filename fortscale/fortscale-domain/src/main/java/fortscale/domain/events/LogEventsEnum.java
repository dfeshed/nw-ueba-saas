package fortscale.domain.events;

public enum LogEventsEnum {
	login("login","login"),
	ssh("ssh","ssh"),
	vpn("vpn","vpn");
	
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
