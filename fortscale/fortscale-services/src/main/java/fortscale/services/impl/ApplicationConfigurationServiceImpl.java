package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.domain.core.dao.ApplicationConfigurationRepositoryImpl;
import fortscale.services.ApplicationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {

    @Autowired
    private ApplicationConfigurationRepository applicationConfigurationRepository;

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

    @Override public ApplicationConfiguration getApplicationConfigurationByKey(String key) {
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
    public Map getApplicationConfigurationByNamespace(String namespace) {
        List<ApplicationConfiguration> applicationConfigurations = applicationConfigurationRepository.
                findByKeyStartsWith(namespace);
        Map<String, String> result = new HashMap();
        if (applicationConfigurations != null) {
            for (ApplicationConfiguration applicationConfiguration: applicationConfigurations) {
                result.put(applicationConfiguration.getKey(), applicationConfiguration.getValue());
            }
        }
        return result;
    }

    @Override
    public Optional<String> readFromConfigurationService(String key) {
        ApplicationConfiguration applicationConfiguration = applicationConfigurationRepository.findOne(key);
        if (applicationConfiguration != null) {
            return Optional.of(applicationConfiguration.getValue());
        }

        return Optional.empty();
    }

}