package fortscale.domain.core.dao;

import fortscale.domain.core.OrganizationActivityLocation;

import java.util.List;

public interface OrganizationActivityRepository {
    List<OrganizationActivityLocation> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit);

    List<OrganizationActivityLocation> findAll();
}
