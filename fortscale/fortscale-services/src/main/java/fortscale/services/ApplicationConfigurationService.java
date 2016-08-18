package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ApplicationConfigurationService {

    List<ApplicationConfiguration> getApplicationConfiguration();
    ApplicationConfiguration getApplicationConfiguration(String key);
    boolean isApplicationConfigurationExists(String key);
    void updateConfigItems (Map<String, String> configItems);
    void updateConfigItemAsObject(String key, Object value);
    void insertConfigItems(Map<String, String> configItems);
    void insertConfigItem(String key, String value);
    void insertConfigItemAsObject(String key, Object value);
    Map getApplicationConfigurationByNamespace(String namespace);
    List<ApplicationConfiguration> getApplicationConfigurationAsListByNamespace(String namespace);
    Optional<String> getApplicationConfigurationAsString(String key);
    <T> List<T> getApplicationConfigurationAsObjects(String configurationKey, Class jsonObjectType);
    <T> T getApplicationConfigurationAsObject(String configurationKey, Class jsonObjectType);

}