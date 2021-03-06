package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LocalizationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {


    private LocalizationService localizationService;
    private ApplicationConfigurationRepository applicationConfigurationRepository;
    private static Logger logger = Logger.getLogger(ApplicationConfigurationServiceImpl.class);
    private static final ObjectMapper objectMapper= new ObjectMapper();

    public ApplicationConfigurationServiceImpl(ApplicationConfigurationRepository applicationConfigurationRepository,
                                               LocalizationService localizationService) {
        this.applicationConfigurationRepository = applicationConfigurationRepository;
        this.localizationService = localizationService;
    }

    /**
     * Returns a list of ApplicationConfiguration documents
     *
     * @return List<ApplicationConfiguration>
     */
    @Override
    public List<ApplicationConfiguration> getApplicationConfiguration() {
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<>();
        applicationConfigurationList.addAll(applicationConfigurationRepository.findAll());
        applicationConfigurationList.addAll(getFromLocalization());

        return applicationConfigurationList;
    }

    private Collection<ApplicationConfiguration> getFromLocalization() {
        Map<String,Map<String, String>>  all = localizationService.getMessagesToAllLanguages();
        List<ApplicationConfiguration> allMessagesList = new ArrayList();

        if (!CollectionUtils.isEmpty(all)){
            for (Map.Entry<String,Map<String, String>> lang : all.entrySet()){
                String langName = lang.getKey();
                Map<String, String> messages = lang.getValue();
                if (!CollectionUtils.isEmpty(messages)){
                    for (Map.Entry<String,String> messageEntry:messages.entrySet()){
                        String originalKey = messageEntry.getKey();
                        String newKey = getNewKey(langName, originalKey);
                        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
                        applicationConfiguration.setKey(newKey);
                        applicationConfiguration.setValue(messageEntry.getValue());
                        allMessagesList.add(applicationConfiguration);

                    }
                }
            }
        }

        return allMessagesList;

    }

    private String getNewKey(String lang, String originalKey) {
        return "messages."+lang+"."+originalKey;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration(String key) {
        return applicationConfigurationRepository.findOneByKey(key);
    }

    @Override
    public boolean isApplicationConfigurationExists(String key) {
        return applicationConfigurationRepository.findOneByKey(key) != null;
    }

    /**
     * Updates or creates config items.
     *
     * @param configItems A map of config items.
     */
    @Override
    public void updateConfigItems (Map<String, String> configItems) {
        applicationConfigurationRepository.updateConfigItems(configItems);
    }

    @Override
    public void   insertConfigItems(Map<String, String> configItems) {
        applicationConfigurationRepository.insertConfigItems(configItems);
    }


    @Override
    public void insertConfigItem(String key, String value) {
        applicationConfigurationRepository.insertConfigItem(key, value);
    }

    @Override
    public void insertConfigItemAsObject(String key, Object value) {

        try {
            insertConfigItem(key, objectMapper.writeValueAsString(value));
        } catch (Exception ex) {
            logger.error("failed to convert object to string - " + ex);
        }
    }

    @Override
    public void insertOrUpdateConfigItemAsObject(String key, Object value) {
        if (isApplicationConfigurationExists(key)){
            updateConfigItemAsObject(key, value);
        }else {
            insertConfigItemAsObject(key, value);
        }
    }

    @Override
    public void updateConfigItemAsObject(String key, Object value) {

        try {
            Map<String,String> items = new HashMap<>();
            items.put(key,objectMapper.writeValueAsString(value));
            this.updateConfigItems(items );
        } catch (Exception ex) {
            logger.error("failed to convert object to string - " + ex);
        }
    }

    @Override
    public Map<String, String> getApplicationConfigurationByNamespace(String namespace) {
        List<ApplicationConfiguration> applicationConfigurations = applicationConfigurationRepository.
                findByKeyStartsWith(namespace);
        Map<String, String> result = new HashMap<>();
        if (applicationConfigurations != null) {
            for (ApplicationConfiguration applicationConfiguration: applicationConfigurations) {
                result.put(applicationConfiguration.getKey(), applicationConfiguration.getValue());
            }
        }
        return result;
    }

    @Override
    public List<ApplicationConfiguration> getApplicationConfigurationAsListByNamespace(String namespace) {
        return applicationConfigurationRepository.
                findByKeyStartsWith(namespace);

    }

    @Override
    public Optional<String> getApplicationConfigurationAsString(String key) {
        ApplicationConfiguration applicationConfiguration = applicationConfigurationRepository.findOneByKey(key);
        if (applicationConfiguration != null) {
            return Optional.of(applicationConfiguration.getValue());
        }

        return Optional.empty();
    }

    /**
     *
     * This method gets a list of all objects with key {@code configurationKey} from the mongoDB
     * @param configurationKey the key of the configuration in mongoDB (e.g - system.activeDirectory.settings)
     * @param jsonObjectType the type of the json object that needs to be read (e.g - AdConnection)
     * @return a list of all objects with key {@code configurationKey} from the mongoDB, if nothing is found returns an empty list
     */
    @Override
    public <T> List<T> getApplicationConfigurationAsObjects(String configurationKey, Class jsonObjectType) {
        List<T> readObjects = new ArrayList<>();
        ApplicationConfiguration applicationConfiguration = getApplicationConfiguration(configurationKey);
        if (applicationConfiguration != null) {
            String config = applicationConfiguration.getValue();
            try {
                readObjects = objectMapper.readValue(config, objectMapper.getTypeFactory().
						constructCollectionType(List.class, jsonObjectType));
            } catch (Exception ex) {
                logger.error("failed to load Active Directory configuration from mongoDB for json object type '{}' and configuration key '{}'",
						jsonObjectType.getName(), configurationKey, ex);
            }
        }
        return readObjects;
    }

    @Override
    public <T> T getApplicationConfigurationAsObject(String configurationKey, Class jsonObjectType) {
        ApplicationConfiguration applicationConfiguration = getApplicationConfiguration(configurationKey);
        T readObject = null;
        if (applicationConfiguration != null) {
            String config = applicationConfiguration.getValue();
            try {
                readObject = (T)objectMapper.readValue(config, jsonObjectType);
            } catch (Exception ex) {
                logger.error("failed to load Active Directory configuration from mongoDB for json object type '{}' and configuration key '{}'",
						jsonObjectType.getName(), configurationKey, ex);
            }
        }

        return readObject;
    }

    @Override
    public Long delete(String key) {
        return applicationConfigurationRepository.deleteByKey(key);
    }

    @Override
    public String getKeyDelimiter() {
        return ".";
    }

}