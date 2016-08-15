package fortscale.services;

import fortscale.domain.core.activities.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);

    List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id,
            int timeRangeInDays);
    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);

    List<UserActivityWorkingHoursDocument> getUserActivityWorkingHoursEntries(String id, int timeRangeInDays);

    List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays);

    List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays);

    List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String id, int timeRangeInDays);

    public Set<String> getUserNamesByUserLocation(List<String> userLocations);
}