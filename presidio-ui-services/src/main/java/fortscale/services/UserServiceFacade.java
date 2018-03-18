package fortscale.services;

import fortscale.domain.core.User;


import java.util.List;
import java.util.Map;



public interface UserServiceFacade {
	public List<User> findBySearchFieldContaining(String prefix, int page, int size);


	public String getUserThumbnail(User user);


	public Boolean isPasswordExpired(User user);

	public Boolean isNoPasswordRequiresValue(User user);

	public Boolean isNormalUserAccountValue(User user);

	public Boolean isPasswordNeverExpiresValue(User user);

	public String getOu(User user);


	public User getUserManager(User user, Map<String, User> dnToUserMap);

	public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap);
}
