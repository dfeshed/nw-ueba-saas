package fortscale.services;

import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserWithAlertService {

	public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest);

	public int countUsersByFilter(UserRestFilter userRestFilter);

	public void recalculateNumberOfUserAlerts(String userName);

}