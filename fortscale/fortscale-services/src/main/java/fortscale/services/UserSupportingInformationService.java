package fortscale.services;

import fortscale.domain.core.User;
import fortscale.domain.core.UserSupprotingInformation;

/**
 * Created by galiar on 20/08/2015.
 */
public interface UserSupportingInformationService {

	public UserSupprotingInformation createUserSupprotingInformation(User user, UserService userService );
}
