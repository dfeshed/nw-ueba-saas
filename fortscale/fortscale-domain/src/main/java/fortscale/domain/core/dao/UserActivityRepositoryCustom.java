package fortscale.domain.core.dao;

import fortscale.domain.core.activities.*;

import java.util.List;

public interface UserActivityRepositoryCustom {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);
	List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username, int timeRangeInDays);
	List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);
	List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays);
	List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String username, int timeRangeInDays);

}
