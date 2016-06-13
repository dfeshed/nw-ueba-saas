package fortscale.domain.core.dao;

import fortscale.domain.core.activities.OrganizationActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.utils.logging.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("UserActivityRepository")
public class UserActivityRepositoryImpl extends UserActivityBaseRepository implements UserActivityRepositoryCustom {

    private static final String COLLECTION_NAME_LOCATION = UserActivityLocationDocument.COLLECTION_NAME;
    private static final String COLLECTION_NAME_NETWORK_AUTHENTICATION = UserActivityNetworkAuthenticationDocument.COLLECTION_NAME;
    public static final String COLLECTION_NAME_ORGANIZATION = OrganizationActivityLocationDocument.COLLECTION_NAME;
    public static final String COLLECTION_NAME_SOURCE_MACHINE = UserActivitySourceMachineDocument.COLLECTION_NAME;
    private static final Logger logger = Logger.getLogger(UserActivityRepositoryImpl.class);


    @Override
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_LOCATION, UserActivityLocationDocument.class);
    }

    @Override
    public List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username, int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_NETWORK_AUTHENTICATION, UserActivityNetworkAuthenticationDocument.class);
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays) {
        return getUserActivityEntries(null, timeRangeInDays, COLLECTION_NAME_ORGANIZATION, OrganizationActivityLocationDocument.class);
    }

    @Override
    public List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String username, int timeRangeInDays){
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_SOURCE_MACHINE, UserActivitySourceMachineDocument.class);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
