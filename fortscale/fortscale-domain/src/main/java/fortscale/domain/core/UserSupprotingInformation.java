package fortscale.domain.core;

import fortscale.domain.core.dao.UserRepository;

import java.util.*;

/**
 * supporting information for tag evidences - the state of the user in the time of the evidence creation.
 * Created by galiar on 12/08/2015.
 */
public class UserSupprotingInformation {

	private String username;
	private String title;
	private String department;
	private User manager;
	private List<User> directReports;

	private boolean normalUserAccount;
	private boolean noPasswordRequired;
	private boolean passwordNeverExpire;

	private String ou;

	List<String> membershipGroups;

	public UserSupprotingInformation(){};

	public UserSupprotingInformation(User user, UserRepository userRepository){

		username = user.getUsername();
		title = user.getAdInfo().getPosition();
		department = user.getAdInfo().getDepartment();

		//preparations for manager and direct reports
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();
		UserUtils.fillUserRelatedDns(user, userRelatedDnsSet);
		UserUtils.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap,userRepository);

		manager = UserUtils.getUserManager(user,dnToUserMap);
		directReports = UserUtils.getUserDirectReports(user,dnToUserMap);

		normalUserAccount = UserUtils.isNormalUserAccountValue(user);
		noPasswordRequired = UserUtils.isNoPasswordRequiresValue(user);
		passwordNeverExpire = UserUtils.isNoPasswordRequiresValue(user);

		ou = UserUtils.getOu(user);

	}

}
