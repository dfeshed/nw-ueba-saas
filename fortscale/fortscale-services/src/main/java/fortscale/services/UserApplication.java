package fortscale.services;

public enum UserApplication {
	active_directory("active_directory","User Profile"),
	vpn("vpn","vpn"),
	ssh("ssh","ssh");
	
	private String id;
	private String displayName;
	
	UserApplication(String id, String displayName) {
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
