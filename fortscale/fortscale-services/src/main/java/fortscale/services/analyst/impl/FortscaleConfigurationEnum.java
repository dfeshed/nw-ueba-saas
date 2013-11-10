package fortscale.services.analyst.impl;

public enum FortscaleConfigurationEnum {
	score("score","Score Configuration");
	
	private String id;
	private String displayName;
	
	FortscaleConfigurationEnum(String id, String displayName) {
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
