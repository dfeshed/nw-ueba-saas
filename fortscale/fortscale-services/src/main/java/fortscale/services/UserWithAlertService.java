package fortscale.services;

import fortscale.common.datastructures.activity.UserActivityData;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserWithAlertService extends CachingService{

	List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired);

	int countUsersByFilter(UserRestFilter userRestFilter);

	void recalculateNumberOfUserAlertsByUserName(String userName);

	void recalculateNumberOfUserAlertsByUserId(String userId);

    List<User> findAndSaveUsersByFilter(UserRestFilter userRestFilter, String searchValue);

	List<UserActivityData.DeviceEntry> getUserActivitySourceMachineDocuments(User user);
}