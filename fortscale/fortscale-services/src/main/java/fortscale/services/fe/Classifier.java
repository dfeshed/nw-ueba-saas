package fortscale.services.fe;

import fortscale.services.LogEventsEnum;
import fortscale.services.exceptions.InvalidValueException;

public enum Classifier {
	ad("active_directory","User Profile",null),
	groups("active_directory_group_membership","Groups",null),
	vpn("vpn","VPN",null),
	auth("auth","Logins",LogEventsEnum.login),
	ssh("ssh","SSH",LogEventsEnum.ssh),
	total("total","Total Score",null);
	
	private String id;
	private String displayName;
	private LogEventsEnum logEventsEnum;
	
	Classifier(String id, String displayName, LogEventsEnum logEventsEnum) {
		this.id = id;
		this.displayName = displayName;
		this.setLogEventsEnum(logEventsEnum);
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public static void validateClassifierId(String classifierId){
		boolean isExist = false;
		for(Classifier classifier: Classifier.values()){
			if(classifierId.equals(classifier.getId())){
				isExist = true;
				break;
			}
		}
		if(!isExist){
			throw new InvalidValueException(String.format("no such classifier id [%s]", classifierId));
		}
	}

	public LogEventsEnum getLogEventsEnum() {
		return logEventsEnum;
	}

	public void setLogEventsEnum(LogEventsEnum logEventsEnum) {
		this.logEventsEnum = logEventsEnum;
	}
	
	
//	public static String getAdClassifierUniqueName(){
//		return "active_directory";
//	}
}
