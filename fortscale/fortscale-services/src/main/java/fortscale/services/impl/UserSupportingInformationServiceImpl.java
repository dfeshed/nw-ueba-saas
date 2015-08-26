package fortscale.services.impl;

import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.core.User;
import fortscale.domain.core.UserSupportingInformation;
import fortscale.services.UserService;
import fortscale.services.UserSupportingInformationService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by galiar on 20/08/2015.
 */
@Service("UserSupportingInformationService")
public class UserSupportingInformationServiceImpl implements UserSupportingInformationService {
	@Override
	public UserSupportingInformation createUserSupportingInformation(User user, UserService userService ){

		UserSupportingInformation userSupportingInformation = new UserSupportingInformation();

		userSupportingInformation.setUsername(user.getAdInfo().getDisplayName());
		userSupportingInformation.setSAMAccountName(user.getAdInfo().getsAMAccountName());
		userSupportingInformation.setAdUserName(user.getAdInfo().getUserPrincipalName());
		userSupportingInformation.setTitle(user.getAdInfo().getPosition());
		userSupportingInformation.setDepartment(user.getAdInfo().getDepartment());

		//generate the admin groups and non admins groups
		Set<AdUserGroup> adminGroups = new HashSet<>();
		Set<AdUserGroup> nonAdminGroup = new HashSet<>();

		for (AdUserGroup group : user.getAdInfo().getGroups())
		{
			if (group.getName().contains("admin"))
				adminGroups.add(group);
			else
				nonAdminGroup.add(group);
		}

		userSupportingInformation.setAdminGroups(adminGroups);
		userSupportingInformation.setNonAdminGroups(adminGroups);

		//preparations for manager and direct reports
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();

		userService.fillUserRelatedDns(user, userRelatedDnsSet);
		userService.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		userSupportingInformation.setManager(userService.getUserManager(user,dnToUserMap).getDisplayName());
		userSupportingInformation.setDirectReports(user.getAdInfo().getDirectReports());
		userSupportingInformation.setNormalUserAccount(userService.isNormalUserAccountValue(user));
		userSupportingInformation.setNoPasswordRequired(userService.isNoPasswordRequiresValue(user));
		userSupportingInformation.setPasswordNeverExpire(userService.isPasswordNeverExpiresValue(user));

		userSupportingInformation.setOu(userService.getOu(user));

		return userSupportingInformation;
	}
}
