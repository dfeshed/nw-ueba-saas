package fortscale.services;

import fortscale.domain.core.User;
import fortscale.domain.core.UserSupportingInformation;

/**
 * Created by galiar on 20/08/2015.
 */
public interface UserSupportingInformationService {

	public UserSupportingInformation createUserSupportingInformation(User user, UserService userService );
}
