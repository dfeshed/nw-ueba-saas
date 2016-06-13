package fortscale.domain.core.activities;

public class UserActivityDocumentFactory {

	//Todo: add all document types
	//Todo: get string from common string file once we create it. the way it's now is bad!
	public static UserActivityDocument getInstanceByActivityName(String activityName) {
		switch (activityName) {
			case "locations" :
				return new UserActivityLocationDocument();
			case "network_authentication" :
				return new UserActivityNetworkAuthenticationDocument();
			case "data_usage" :
				return new UserActivityDataUsageDocument();
			default:
				throw new IllegalArgumentException("Illegal activity name");
		}
	}

}