package fortscale.domain.core.dao;

import fortscale.domain.core.OrganizationActivityLocationDocument;

import java.util.List;

public interface OrganizationActivityRepository {
    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit);

    List<OrganizationActivityLocationDocument> findAll();
}
