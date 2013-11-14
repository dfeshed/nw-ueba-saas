package fortscale.services.fe;

import fortscale.services.exceptions.InvalidValueException;

public enum Classifier {
	ad("active_directory","User Profile"),
	groups("active_directory_group_membership","Groups"),
	vpn("vpn","VPN"),
	auth("auth","Logins"),
	total("total","Total Score");
	
	private String id;
	private String displayName;
	
	Classifier(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
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
	
	
//	public static String getAdClassifierUniqueName(){
//		return "active_directory";
//	}
}
