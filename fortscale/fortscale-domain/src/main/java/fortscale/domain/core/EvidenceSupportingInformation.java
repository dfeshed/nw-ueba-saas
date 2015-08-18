package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Supporting information for evidence
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
