package fortscale.services;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.types.PropertiesDistribution;
import fortscale.utils.JksonSerilaizablePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserWithAlertService {

	public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest);

	public int countUsersByFilter(UserRestFilter userRestFilter);

	public void recalculateNumberOfUserAlerts(String userName);

}