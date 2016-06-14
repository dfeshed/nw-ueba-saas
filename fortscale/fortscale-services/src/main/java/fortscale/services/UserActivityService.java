package fortscale.services;

import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;

import java.util.List;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);

    List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id, int timeRangeInDays);

    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays);
}
