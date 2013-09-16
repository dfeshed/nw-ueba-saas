package fortscale.services.fe;

public enum Classifier {
	ad("active_directory","User Profile"),
	auth("auth","Logins");
	
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
	
	
//	public static String getAdClassifierUniqueName(){
//		return "active_directory";
//	}
}
