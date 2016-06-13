package fortscale.services;

import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityDataUsageDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;

import java.util.List;
import java.util.Map;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);
    List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id,
            int timeRangeInDays);
    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);
    List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String id, int timeRangeInDays);

}