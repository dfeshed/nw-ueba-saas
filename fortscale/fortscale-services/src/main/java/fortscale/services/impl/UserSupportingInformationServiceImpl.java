package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.domain.core.UserSupportingInformation;
import fortscale.services.UserService;
import fortscale.services.UserSupportingInformationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by galiar on 20/08/2015.
 */
@Service("UserSupportingInformationService")
public class UserSupportingInformationServiceImpl implements UserSupportingInformationService {
	@Override
	public UserSupportingInformation createUserSupportingInformation(User user, UserService userService ){

		UserSupportingInformation userSupportingInformation = new UserSupportingInformation();

		userSupportingInformation.setUsername(user.getUsername());
		userSupportingInformation.setTitle(user.getAdInfo().getPosition());
		userSupportingInformation.setDepartment(user.getAdInfo().getDepartment());

		//preparations for manager and direct reports
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();

		userService.fillUserRelatedDns(user, userRelatedDnsSet);
		userService.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		userSupportingInformation.setManager(userService.getUserManager(user,dnToUserMap));
		userSupportingInformation.setDirectReports(userService.getUserDirectReports(user,dnToUserMap));
		userSupportingInformation.setNormalUserAccount(userService.isNormalUserAccountValue(user));
		userSupportingInformation.setNoPasswordRequired(userService.isNoPasswordRequiresValue(user));
		userSupportingInformation.setPasswordNeverExpire(userService.isPasswordNeverExpiresValue(user));

		userSupportingInformation.setOu(userService.getOu(user));

		return userSupportingInformation;
	}
}
