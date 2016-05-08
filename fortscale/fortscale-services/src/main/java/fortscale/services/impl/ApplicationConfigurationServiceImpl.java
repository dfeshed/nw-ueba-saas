package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.cache.CacheHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService, InitializingBean {

    @Autowired
    @Qualifier("configurationCache")
    private CacheHandler<String, ApplicationConfiguration> cache;

    @Autowired
    private ApplicationConfigurationRepository applicationConfigurationRepository;

    /**
     * Returns a list of ApplicationConfiguration documents
     *
     * @return List<ApplicationConfiguration>
     */
    @Override
    public List<ApplicationConfiguration> getApplicationConfiguration() {
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList();
        Map<String, ApplicationConfiguration> applicationConfigurationMap = cache.getAll();
        if (applicationConfigurationMap != null && !applicationConfigurationMap.isEmpty()) {
            applicationConfigurationList.addAll(applicationConfigurationMap.values());
        } else {
            List<ApplicationConfiguration> applicationConfigurations = applicationConfigurationRepository.findAll();
            for (ApplicationConfiguration applicationConfiguration: applicationConfigurations) {
                cache.put(applicationConfiguration.getKey(), applicationConfiguration);
            }
            applicationConfigurationList.addAll(applicationConfigurations);
        }
        return applicationConfigurationList;
    }

    @Override
    public ApplicationConfiguration getApplicationConfigurationByKey(String key) {
        ApplicationConfiguration applicationConfiguration = cache.get(key);
        if (applicationConfiguration == null) {
            applicationConfiguration = applicationConfigurationRepository.findOneByKey(key);
            cache.put(key, applicationConfiguration);
        }
        return applicationConfiguration;
    }

    /**
     * Updates or creates config items.
     *
     * @param configItems A map of config items.
     */
    @Override
    public void updateConfigItems(Map<String, String> configItems) {
        for (Map.Entry<String, String> entry: configItems.entrySet()) {
            cache.put(entry.getKey(), new ApplicationConfiguration(entry.getKey(), entry.getValue()));
        }
        applicationConfigurationRepository.updateConfigItems(configItems);
    }

    @Override
    public void insertConfigItems(Map<String, String> configItems) {
        for (Map.Entry<String, String> entry: configItems.entrySet()) {
            cache.put(entry.getKey(), new ApplicationConfiguration(entry.getKey(), entry.getValue()));
        }
        applicationConfigurationRepository.insertConfigItems(configItems);
    }

    @Override
    public void insertConfigItem(String key, String value) {
        cache.put(key, new ApplicationConfiguration(key, value));
        applicationConfigurationRepository.insertConfigItem(key, value);
    }

    @Override
    public Map getApplicationConfigurationByNamespace(String namespace) {
        Map<String, String> result = new HashMap();
        Map<String, ApplicationConfiguration> applicationConfigurationMap = cache.getAll();
        if (applicationConfigurationMap != null && !applicationConfigurationMap.isEmpty()) {
            applicationConfigurationMap.entrySet().stream().filter(entry -> entry.getKey().startsWith(namespace)).
                    forEach(entry -> result.put(entry.getKey(), entry.getValue().getValue()));
        } else {
            List<ApplicationConfiguration> applicationConfigurations = applicationConfigurationRepository.
                    findByKeyStartsWith(namespace);
            if (applicationConfigurations != null) {
                for (ApplicationConfiguration applicationConfiguration: applicationConfigurations) {
                    cache.put(applicationConfiguration.getKey(), applicationConfiguration);
                    result.put(applicationConfiguration.getKey(), applicationConfiguration.getValue());
                }
            }
        }
        return result;
    }

    @Override
    public Optional<String> readFromConfigurationService(String key) {
        ApplicationConfiguration applicationConfiguration = getApplicationConfigurationByKey(key);
        if (applicationConfiguration != null) {
            return Optional.of(applicationConfiguration.getValue());
        }
        return Optional.empty();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cache.clear();
        List<ApplicationConfiguration> applicationConfigurationList = getApplicationConfiguration();
        for (ApplicationConfiguration applicationConfiguration: applicationConfigurationList) {
            cache.put(applicationConfiguration.getKey(), applicationConfiguration);
        }
    }

}