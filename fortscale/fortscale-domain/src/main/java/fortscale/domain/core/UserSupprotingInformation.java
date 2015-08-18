package fortscale.domain.core;

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

	private UserUtils userUtils;

	public UserSupprotingInformation(){};

	public UserSupprotingInformation(User user){
		this.userUtils = new UserUtils();

		username = user.getUsername();
		title = user.getAdInfo().getPosition();
		department = user.getAdInfo().getDepartment();

		//preparations for manager and direct reoprts
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();
		userUtils.fillUserRelatedDns(user, userRelatedDnsSet);
		userUtils.fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);

		manager = userUtils.getUserManager(user,dnToUserMap);
		directReports = userUtils.getUserDirectReports(user,dnToUserMap);

		normalUserAccount = userUtils.isNormalUserAccountValue(user);
		noPasswordRequired = userUtils.isNoPasswordRequiresValue(user);
		passwordNeverExpire = userUtils.isNoPasswordRequiresValue(user);

		ou = userUtils.getOu(user);

	}

}
