package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.domain.core.UserSupprotingInformation;
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
	public UserSupprotingInformation createUserSupprotingInformation(User user, UserService userService ){

		UserSupprotingInformation userSupprotingInformation = new UserSupprotingInformation();

		userSupprotingInformation.setUsername(user.getUsername());
		userSupprotingInformation.setTitle(user.getAdInfo().getPosition());
		userSupprotingInformation.setDepartment(user.getAdInfo().getDepartment());

		//preparations for manager and direct reports
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();

		userService.fillUserRelatedDns(user, userRelatedDnsSet);
		userService.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		userSupprotingInformation.setManager(userService.getUserManager(user,dnToUserMap));
		userSupprotingInformation.setDirectReports(userService.getUserDirectReports(user,dnToUserMap));
		userSupprotingInformation.setNormalUserAccount(userService.isNormalUserAccountValue(user));
		userSupprotingInformation.setNoPasswordRequired(userService.isNoPasswordRequiresValue(user));
		userSupprotingInformation.setPasswordNeverExpire(userService.isPasswordNeverExpiresValue(user));

		userSupprotingInformation.setOu(userService.getOu(user));

		return userSupprotingInformation;
	}
}
