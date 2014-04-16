package fortscale.domain.core;

public class NotificationAggregateFsIdInfo {
	private String name;
	private String displayName;
	private String fsId;
	
	
	public NotificationAggregateFsIdInfo(Notification notification){
		name = notification.getName();
		displayName = notification.getDisplayName();
		fsId = notification.getFsId();
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
	
	
	
}
