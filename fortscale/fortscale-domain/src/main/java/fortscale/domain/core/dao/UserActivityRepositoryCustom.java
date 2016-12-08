package fortscale.domain.core.dao;

import fortscale.domain.core.activities.*;

import java.util.List;
import java.util.Set;

public interface UserActivityRepositoryCustom {

	List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays);
	List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username,
			int timeRangeInDays);
	List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String username, int timeRangeInDays);
	List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);
	List<UserActivityWorkingHoursDocument> getUserActivityWorkingHoursEntries(String username, int timeRangeInDays);
	List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays);
	List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String username, int timeRangeInDays);
	Set<String> getUserIdByLocation(List<String> locations);

}
