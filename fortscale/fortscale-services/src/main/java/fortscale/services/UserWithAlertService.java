package fortscale.services;

import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserWithAlertService{

	List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired);

	int countUsersByFilter(UserRestFilter userRestFilter);

	void recalculateNumberOfUserAlertsByUserName(String userName);

	void recalculateNumberOfUserAlertsByUserId(String userId);

    List<User> findFromCacheUsersByFilter(UserRestFilter userRestFilter);

    int updateTags(UserRestFilter userRestFilter, Boolean addTag, List<String> tagNames) throws Exception;

	int followUsersByFilter(UserRestFilter userRestFilter, Boolean watch);
}