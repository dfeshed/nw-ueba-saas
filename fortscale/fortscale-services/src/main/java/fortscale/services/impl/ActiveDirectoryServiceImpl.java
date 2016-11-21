package fortscale.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.dao.*;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("ActiveDirectoryService")
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService, InitializingBean {

    private static Logger logger = Logger.getLogger(ActiveDirectoryServiceImpl.class);

    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";

    @Value("${ad.connections}")
    private String adConnectionsFile;



    private final ActiveDirectoryDAO activeDirectoryDAO;
    private final ApplicationConfigurationService applicationConfigurationService;
    private final AdGroupRepository adGroupRepository;
    private final AdOURepository adOURepository;
    private final AdUserRepository adUserRepository;
    private final AdComputerRepository adComputerRepository;


    @Autowired
    public ActiveDirectoryServiceImpl(
                                    ActiveDirectoryDAO activeDirectoryDAO,
                                    ApplicationConfigurationService applicationConfigurationService,
                                    AdGroupRepository adGroupRepository,
                                    AdOURepository adOURepository,
                                    AdUserRepository adUserRepository,
                                    AdComputerRepository adComputerRepository) {
        this.adGroupRepository = adGroupRepository;
        this.adOURepository = adOURepository;
        this.adUserRepository = adUserRepository;
        this.adComputerRepository = adComputerRepository;
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
            adConnections = applicationConfigurationService.
                    getApplicationConfigurationAsObjects(AdConnection.ACTIVE_DIRECTORY_KEY, AdConnection.class);
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
    public void saveAdConnectionsInDatabase(List<AdConnection> adConnections) {
        applicationConfigurationService.updateConfigItemAsObject(AdConnection.ACTIVE_DIRECTORY_KEY,adConnections);
    }

    @Override
    public String canConnect(AdConnection adConnection) {
        String result;
        try {
            result = activeDirectoryDAO.connectToAD(adConnection);
        } catch (Exception ex) {
            logger.error("failed to connect to ad - {}", ex);
            result = ex.getLocalizedMessage();
        }
        return result;
    }

    /**
     * This method queries the {@link AdGroup} collection and returns a list of {@link AdGroup}s whose 'name' field contains the given {@param contains}.
     * This method is case-insensitive
     *
     * @param contains the string that {@link AdGroup}'s 'name' field needs to contain
     * @return         a list of {@link AdGroup}s whose 'name' field contains the given {@param contains}
     */
    @Override
    public List<AdGroup> getGroupsByNameContains(String contains) {
        return adGroupRepository.findByNameLikeIgnoreCase(contains);
    }

    /**
     * This method queries the {@link AdOU} collection and returns a list of {@link AdOU}s whose 'ou' field contains the given {@param contains}.
     * This method is case-insensitive
     * @param contains the string that {@link AdOU}'s 'ou' field needs to contain
     * @return         a list of {@link AdOU}s whose 'ou' field contains the given {@param contains}
     */
    @Override
    public List<AdOU> getOusByOuContains(String contains) {
        return adOURepository.findByOuLikeIgnoreCase(contains);
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
            domainControllers = new ArrayList<>(Arrays.asList(applicationConfigurationService.
                    getApplicationConfigurationAsString(DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY)
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

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!applicationConfigurationService.isApplicationConfigurationExists(AdConnection.ACTIVE_DIRECTORY_KEY)) {
            //initialize with default test values if no configuration key exists
            logger.warn("Active Directory configuration not found, trying to load configuration from file");
            List<AdConnection> adConnections;
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(adConnectionsFile);
            if (!jsonFile.exists()) {
                logger.error("AdConnections json file does not exist");
                return;
            }
            try {
                adConnections = mapper.readValue(jsonFile, new TypeReference<List<AdConnection>>(){});
            } catch (Exception ex) {
                logger.error("Error - Bad Active Directory Json connection file");
                throw new Exception(ex);
            }
            applicationConfigurationService.insertConfigItemAsObject(AdConnection.ACTIVE_DIRECTORY_KEY, adConnections);
        }
    }

}