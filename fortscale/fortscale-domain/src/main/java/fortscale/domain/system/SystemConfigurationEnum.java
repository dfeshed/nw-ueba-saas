package fortscale.domain.system;

public enum SystemConfigurationEnum {
	dc("dc");
	
	private String id;
	
	SystemConfigurationEnum(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
