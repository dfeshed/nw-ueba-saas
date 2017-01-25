package fortscale.services.impl;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject.AdObjectType;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.dao.*;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("ActiveDirectoryService")
public class ActiveDirectoryServiceImpl implements ActiveDirectoryService {

    private static Logger logger = Logger.getLogger(ActiveDirectoryServiceImpl.class);

    private static final String DB_DOMAIN_CONTROLLERS_CONFIGURATION_KEY = "system.activeDirectory.domainControllers";


    private final ActiveDirectoryDAO activeDirectoryDAO;
    private final ApplicationConfigurationService applicationConfigurationService;
    private final AdGroupRepository adGroupRepository;
    private final AdOURepository adOURepository;
    private final AdUserRepository adUserRepository;
    private final AdComputerRepository adComputerRepository;
    private final AdUserThumbnailRepository adUserThumbnailRepository;

    @Autowired
    public ActiveDirectoryServiceImpl(
            ActiveDirectoryDAO activeDirectoryDAO,
            ApplicationConfigurationService applicationConfigurationService,
            AdGroupRepository adGroupRepository,
            AdOURepository adOURepository,
            AdUserRepository adUserRepository,
            AdComputerRepository adComputerRepository, AdUserThumbnailRepository adUserThumbnailRepository) {
        this.adGroupRepository = adGroupRepository;
        this.adOURepository = adOURepository;
        this.adUserRepository = adUserRepository;
        this.adComputerRepository = adComputerRepository;
        this.activeDirectoryDAO = activeDirectoryDAO;
        this.applicationConfigurationService = applicationConfigurationService;
        this.adUserThumbnailRepository = adUserThumbnailRepository;
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
    public boolean canConnect(AdConnection adConnection) throws NamingException, PasswordDecryptionException{
        boolean success;
        try {
            success = activeDirectoryDAO.connectToAD(adConnection);
        } catch (NamingException | PasswordDecryptionException ex) {
            logger.error("failed to connect to ad - {}", ex);
            throw ex;
        }
        return success;
    }


    @Override
    public Long getCount(AdObjectType adObjectType) {
        switch (adObjectType) {
            case GROUP:
                return adGroupRepository.count();
            case OU:
                return adOURepository.count();
            case USER:
                return adUserRepository.count();
            case COMPUTER:
                return adComputerRepository.count();
            case USER_THUMBNAIL:
                return adUserThumbnailRepository.count();
            default:
                throw new IllegalArgumentException(String.format("Invalid AD object type %s. Valid types are: %s", adObjectType, Arrays.toString(AdObjectType.values())));
        }

    }

    @Override
    public AdUserThumbnail findAdUserThumbnailById(String objectGUID) {
        return adUserThumbnailRepository.findById(objectGUID);
    }

    @Override
    public List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails) {
        return adUserThumbnailRepository.save(adUserThumbnails);
    }

    /**
     * This method queries the {@link AdGroup} collection and returns a list of {@link AdGroup}s whose 'name' field contains the given {@param contains}.
     * This method is case-insensitive
     *
     * @param contains the string that {@link AdGroup}'s 'name' field needs to contain
     * @return         a list of {@link AdGroup}s whose 'name' field contains the given {@param contains}
     */
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
    public String getLatestRuntime(AdObjectType adObjectType) {
        switch (adObjectType) {
            case GROUP:
                return adGroupRepository.getLatestRuntime();
            case OU:
                return adOURepository.getLatestRuntime();
            case USER:
                return adUserRepository.getLatestRuntime();
            case COMPUTER:
                return adComputerRepository.getLatestRuntime();
            default:
                throw new IllegalArgumentException(String.format("Invalid AD object type %s. Valid types are: %s", adObjectType, Arrays.toString(AdObjectType.values())));
        }
    }

    @Override
    public Long countByRuntime(AdObjectType adObjectType, String latestRuntime) {
        switch (adObjectType) {
            case GROUP:
                return adGroupRepository.countByRuntime(latestRuntime);
            case OU:
                return adOURepository.countByRuntime(latestRuntime);
            case USER:
                return adUserRepository.countByRuntime(latestRuntime);
            case COMPUTER:
                return adComputerRepository.countByRuntime(latestRuntime);
            case USER_THUMBNAIL:
                return adUserThumbnailRepository.count();
            default:
                final ArrayList<AdObjectType> validAdObjectTypes = new ArrayList<>(Arrays.asList(AdObjectType.values()));
                validAdObjectTypes.remove(AdObjectType.USER_THUMBNAIL);
                throw new IllegalArgumentException(String.format("Invalid AD object type %s. Valid types are: %s", adObjectType, validAdObjectTypes));
        }
    }

    @Override
    public Long getLastRunCount(AdObjectType adObjectType) {
        if (adObjectType == AdObjectType.USER_THUMBNAIL) {
            return adUserThumbnailRepository.count();
        }
        String latestRuntime = getLatestRuntime(adObjectType);
        final Long currObjectsCount = countByRuntime(adObjectType, latestRuntime);
        return currObjectsCount;
    }
}