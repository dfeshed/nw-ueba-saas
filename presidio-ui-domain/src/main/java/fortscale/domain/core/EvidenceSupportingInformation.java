package fortscale.domain.core;

/**
 * Supporting information for evidence - saved in the evidence in mongo.
 * currently being used only by the tag evidence to user supporting information
 * Date: 7/2/2015.
 */
public class EvidenceSupportingInformation {

	private UserSupportingInformation userSupportingInformation;

	public UserSupportingInformation getUserDetails() {
		return userSupportingInformation;
	}

	public void setUserDetails(UserSupportingInformation userDetails) {
		this.userSupportingInformation = userDetails;
	}


}
