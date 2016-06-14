package fortscale.domain.core.dao;

import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;

import java.util.List;

public interface UserActivityRepositoryCustom {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);
	List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username, int timeRangeInDays);
	List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);
	List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays);

}
