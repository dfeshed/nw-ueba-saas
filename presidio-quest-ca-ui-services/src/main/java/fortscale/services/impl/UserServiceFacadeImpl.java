package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("userServiceFacade")
public class UserServiceFacadeImpl implements UserServiceFacade{
	
	@Autowired
	private UserService userService;
//	@Autowired
//	private UserUpdateScoreService userUpdateScoreService;
//	@Autowired
//	private UserScoreService userScoreService;
//	@Autowired
//	private UsernameService usernameService;

	@Override
	public List<User> findBySearchFieldContaining(String prefix, int page, int size) {
		return userService.findBySearchFieldContaining(prefix, page, size);
	}
	


	
	@Override
	public String getUserThumbnail(User user) {
		return userService.getUserThumbnail(user);
	}




	@Override public Boolean isPasswordExpired(User user) {
		return userService.isPasswordExpired(user);
	}

	@Override public Boolean isNoPasswordRequiresValue(User user) {
		return userService.isNoPasswordRequiresValue(user);
	}

	@Override public Boolean isNormalUserAccountValue(User user) {
		return userService.isNormalUserAccountValue(user);
	}

	@Override public Boolean isPasswordNeverExpiresValue(User user) {
		return userService.isPasswordNeverExpiresValue(user);
	}

	@Override public String getOu(User user) {
		return userService.getOu(user);
	}




	@Override public User getUserManager(User user, Map<String, User> dnToUserMap) {
		return userService.getUserManager(user,dnToUserMap);
	}

	@Override public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap) {
		return userService.getUserDirectReports(user, dnToUserMap);
	}


}
