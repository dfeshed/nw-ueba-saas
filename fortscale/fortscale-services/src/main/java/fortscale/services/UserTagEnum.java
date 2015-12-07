package fortscale.services;

public enum UserTagEnum {
	admin("admin","Administrator"),
	executive("executive","Executive"),
	service("service","Service"),
	LR("LR", "LR"),
	custom("custom", "Custom");

	private String id;
	private String displayName;
	
	UserTagEnum(String id, String displayName) {
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
