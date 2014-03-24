package fortscale.geoip;

public enum IpUsageTypeEnum {
	isp("isp","Fixed Line ISP"),
	mob("mob","Mobile ISP");
	
	private String id;
	private String description;
	
	IpUsageTypeEnum(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
