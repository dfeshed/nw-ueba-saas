package fortscale.services;

import fortscale.domain.core.OrganizationActivityLocation;

import java.util.List;

public interface OrganizationActivityService {

    List<OrganizationActivityLocation> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit);
}
