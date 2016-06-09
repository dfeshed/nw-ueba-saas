package fortscale.domain.core;

public enum UserTagEnum {
	admin("admin","Admin"),
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
