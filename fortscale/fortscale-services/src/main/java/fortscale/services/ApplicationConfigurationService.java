package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ApplicationConfigurationService {

    List<ApplicationConfiguration> getApplicationConfiguration();
    ApplicationConfiguration getApplicationConfiguration(String key);
    void updateConfigItems (Map<String, String> configItems);
    void insertConfigItems(Map<String, String> configItems);
    void insertConfigItem(String key, String value);
    Map getApplicationConfigurationByNamespace(String namespace);
    Optional<String> getApplicationConfigurationAsString(String key);
    <T> List<T> getApplicationConfigurationAsObjects(String configurationKey, Class jsonObjectType);

}