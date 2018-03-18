package fortscale.services;

import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserWithAlertService{

	List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired,boolean fetchUserslerts);

	int countUsersByFilter(UserRestFilter userRestFilter);



    int updateTags(UserRestFilter userRestFilter, Boolean addTag, List<String> tagNames) throws Exception;

	int followUsersByFilter(UserRestFilter userRestFilter, Boolean watch);


}