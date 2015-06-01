package fortscale.collection.tagging.service;

public enum UserTagEnum {
	admin("admin","Administrator Account"),
	executive("executive","Executive Account"),
	service("service","Service Account"),
	LR("LR", "LR");

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
