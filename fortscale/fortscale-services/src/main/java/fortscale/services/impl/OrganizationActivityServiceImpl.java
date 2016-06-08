package fortscale.services.impl;

import fortscale.domain.core.OrganizationActivityLocationDocument;
import fortscale.domain.core.dao.OrganizationActivityRepository;
import fortscale.services.OrganizationActivityService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("OrganizationActivityService")
public class OrganizationActivityServiceImpl implements OrganizationActivityService {

    private static final Logger logger = Logger.getLogger(OrganizationActivityServiceImpl.class);
    private final OrganizationActivityRepository organizationActivityRepository;

    @Autowired
    public OrganizationActivityServiceImpl(OrganizationActivityRepository organizationActivityRepository) {
        this.organizationActivityRepository = organizationActivityRepository;
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit) {
        return organizationActivityRepository.getOrganizationActivityLocationEntries(timeRangeInDays, limit);
    }


}
