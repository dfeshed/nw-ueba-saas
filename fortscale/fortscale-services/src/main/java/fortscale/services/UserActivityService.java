package fortscale.services;

import fortscale.domain.core.activities.*;

import java.util.List;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);

    List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id, int timeRangeInDays);

    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);

    List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays);

    List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays);
}
