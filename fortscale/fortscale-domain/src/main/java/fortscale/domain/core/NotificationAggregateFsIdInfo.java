package fortscale.domain.core;

import java.util.Map;

public class NotificationAggregateFsIdInfo {
	private String name;
	private String displayName;
	private String fsId;



	private Map<String, String> attributes;
	
	
	public NotificationAggregateFsIdInfo(Notification notification){
		name = notification.getName();
		displayName = notification.getDisplayName();
		fsId = notification.getFsId();
		this.attributes = notification.getAttributes();
	}
	
	public String getName() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getFsId() {
		return fsId;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	
	
}
