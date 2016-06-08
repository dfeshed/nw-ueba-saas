package fortscale.domain.core.activities;

public class UserActivityDocumentFactory {

	//Todo: add all document types
	//Todo: get string from common string file once we create it. the way it's new is bad!
	public static UserActivityDocument getInstanceByActivityName(String activityName) {
		switch (activityName) {
			case "location" :
				return new UserActivityLocationDocument();
			case "network_authentication" :
				return new UserActivityNetworkAuthenticationDocument();
			default:
				throw new IllegalArgumentException("Illegal activity name");
		}
	}
}
