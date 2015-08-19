package fortscale.domain.core;

/**
 * Supporting information for evidence - saved in the evidence in mongo.
 * currently being used only by the tag evidence to user supporting information
 * Date: 7/2/2015.
 */
public class EvidenceSupportingInformation {

	private UserSupprotingInformation userSupportingInformation;

	public UserSupprotingInformation getUserDetails() {
		return userSupportingInformation;
	}

	public void setUserDetails(UserSupprotingInformation userDetails) {
		this.userSupportingInformation = userDetails;
	}


}
