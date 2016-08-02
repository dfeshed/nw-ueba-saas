package fortscale.services;

import fortscale.domain.core.activities.*;

import java.util.List;
import java.util.Map;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);

    List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id,
            int timeRangeInDays);
    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);

    List<UserActivityWorkingHoursDocument> getUserActivityWorkingHoursEntries(String id, int timeRangeInDays);

    List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, Integer timeRangeInDays);

    List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays);

    List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String id, int timeRangeInDays);

}