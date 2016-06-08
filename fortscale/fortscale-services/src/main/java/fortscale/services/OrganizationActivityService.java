package fortscale.services;

import fortscale.domain.core.activities.OrganizationActivityLocationDocument;

import java.util.List;

public interface OrganizationActivityService {

    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit);
}
