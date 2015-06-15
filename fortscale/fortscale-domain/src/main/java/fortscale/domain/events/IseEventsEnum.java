package fortscale.domain.events;

public enum IseEventsEnum {
	ipAllocation("ipAllocation", "ipAllocation"),
	ipRelease("ipRelease", "ipRelease"),
	unknown("unknown", "unknown");

	private String id;
	private String displayName;

	IseEventsEnum(String id, String displayName) {
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