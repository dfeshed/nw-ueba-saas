package fortscale.services.fe;

import fortscale.domain.events.LogEventsEnum;
import fortscale.services.UserApplication;
import fortscale.services.exceptions.InvalidValueException;

public enum Classifier {
	ad("active_directory","User Profile",null, UserApplication.active_directory),
	groups("active_directory_group_membership","Groups",null, UserApplication.active_directory),
	vpn("vpn","VPN",LogEventsEnum.vpn, UserApplication.vpn),
	auth("auth","Logins",LogEventsEnum.login, UserApplication.active_directory),
	ssh("ssh","SSH",LogEventsEnum.ssh, UserApplication.ssh),
	total("total","Total Score",null, null);
	
	private String id;
	private String displayName;
	private LogEventsEnum logEventsEnum;
	private UserApplication userApplication;
	
	Classifier(String id, String displayName, LogEventsEnum logEventsEnum, UserApplication userApplication) {
		this.id = id;
		this.displayName = displayName;
		this.setLogEventsEnum(logEventsEnum);
		this.userApplication = userApplication;
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

	public UserApplication getUserApplication() {
		return userApplication;
	}

	public void setUserApplication(UserApplication userApplication) {
		this.userApplication = userApplication;
	}
	
	
	
	
//	public static String getAdClassifierUniqueName(){
//		return "active_directory";
//	}
}
