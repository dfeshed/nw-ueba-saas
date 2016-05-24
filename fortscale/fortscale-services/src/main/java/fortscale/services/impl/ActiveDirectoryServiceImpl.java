package fortscale.services.impl;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.dao.ActiveDirectoryDAO;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("ActiveDirectoryService")
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {

    private final ActiveDirectoryDAO activeDirectoryDAO;
    private final ApplicationConfigurationService applicationConfigurationService;
    private static final String AD_CONNECTIONS_CONFIGURATION_KEY = "system.activeDirectory.settings";
    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";
    private static Logger logger = Logger.getLogger(ActiveDirectoryServiceImpl.class);

    @Autowired
    public ActiveDirectoryServiceImpl(ActiveDirectoryDAO activeDirectoryDAO, ApplicationConfigurationService applicationConfigurationService) {
        this.activeDirectoryDAO = activeDirectoryDAO;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    /**
     * This method performs an active directory query
     *
     * @param filter      The Active Directory search filter (which object class is required)
     * @param adFields    The Active Directory attributes to return in the search
     * @param resultLimit A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
     */
    public void getFromActiveDirectory(String filter, String
            adFields, int resultLimit, ActiveDirectoryResultHandler handler) throws Exception {
        final List<AdConnection> adConnectionsFromDatabase = getAdConnectionsFromDatabase();
        activeDirectoryDAO.getAndHandle(filter, adFields, resultLimit, handler, adConnectionsFromDatabase);
    }


    /**
     * This method gets all the AD connections from the database
     *
     * @return a list of all the AD connections
     */
    public List<AdConnection> getAdConnectionsFromDatabase() {
        List<AdConnection> adConnections = new ArrayList<>();
        try {
            adConnections = applicationConfigurationService.getApplicationConfigurationAsObjects(AD_CONNECTIONS_CONFIGURATION_KEY, AdConnection.class);
        } catch (Exception e) {
            logger.error("Failed to get AD connections from database");
        }
        return adConnections;
    }


    @Override
    public void saveDomainControllersInDatabase(List<String> domainControllers) {
        logger.debug("Saving domain controllers in database");
        String value = String.join(",", domainControllers);
        applicationConfigurationService.insertConfigItem(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY, value);
    }

    @Override
    public List<String> getDomainControllers() {
        List<String> domainControllers = new ArrayList<>();
        try {
            logger.info("Trying to retrieve Domain Controllers from DB");
            domainControllers = getDomainControllersFromDatabase();
            if (domainControllers.isEmpty()) {
                logger.warn("No Domain Controllers were found in DB. Trying to retrieve DCs from Active Directory");
                domainControllers = getDomainControllersFromActiveDirectory();
                if (!domainControllers.isEmpty()) {
                    logger.debug("Found domain controllers in Active Directory");
                    saveDomainControllersInDatabase(domainControllers);
                } else {
                    logger.warn("No domain Controllers were found in Active Directory");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve domain controllers");
        }

        return domainControllers;
    }


    /**
     * This method gets all the AD domain controllers from the database
     *
     * @return a list of all the AD domain controllers
     */
    private List<String> getDomainControllersFromDatabase() {
        List<String> domainControllers = new ArrayList<>();
        try {
            domainControllers = new ArrayList<>(Arrays.asList(applicationConfigurationService.getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)
                    .map(s -> s.split(","))
                    .orElse(new String[0])));
        } catch (Exception e) {
            logger.error("Failed to get AD domain controllers from database");
        }
        return domainControllers;
    }

    private List<String> getDomainControllersFromActiveDirectory() throws Exception {
        final List<AdConnection> adConnectionsFromDatabase = getAdConnectionsFromDatabase();
        return activeDirectoryDAO.getDomainControllers(adConnectionsFromDatabase);
    }
}
