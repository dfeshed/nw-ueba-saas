package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {

    @Autowired
    private ApplicationConfigurationRepository applicationConfigurationRepository;
    private static Logger logger = Logger.getLogger(ApplicationConfigurationServiceImpl.class);
    private ObjectMapper objectMapper= new ObjectMapper();

    /**
     * Returns a list of ApplicationConfiguration documents
     *
     * @return List<ApplicationConfiguration>
     */
    @Override
    public List<ApplicationConfiguration> getApplicationConfiguration() {
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<>();
        applicationConfigurationList.addAll(applicationConfigurationRepository.findAll());
        return applicationConfigurationList;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration(String key) {
        return applicationConfigurationRepository.findOneByKey(key);
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
    public void insertConfigItems(Map<String, String> configItems) {
        applicationConfigurationRepository.insertConfigItems(configItems);
    }

    @Override
    public void insertConfigItem(String key, String value) {
        applicationConfigurationRepository.insertConfigItem(key, value);
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
                readObjects = objectMapper.readValue(config, objectMapper.getTypeFactory().constructCollectionType(List.class, jsonObjectType));
            } catch (Exception ex) {
                logger.error("failed to load Active Directory configuration from mongoDB for json object type '{}' and configuration key '{}'",jsonObjectType.getName(), configurationKey, ex);
            }
        }
        return readObjects;
    }

}