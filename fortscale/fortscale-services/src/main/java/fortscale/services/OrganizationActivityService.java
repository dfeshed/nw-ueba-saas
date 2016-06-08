package fortscale.services;

import fortscale.domain.core.OrganizationActivityLocationDocument;

import java.util.List;

public interface OrganizationActivityService {

    List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit);
}
