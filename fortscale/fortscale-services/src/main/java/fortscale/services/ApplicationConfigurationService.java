package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;

import java.util.List;
import java.util.Map;

public interface ApplicationConfigurationService {

    List<ApplicationConfiguration> getApplicationConfiguration();
    ApplicationConfiguration getApplicationConfigurationByKey(String key);
    void updateConfigItems (Map<String, String> configItems);
    void insertConfigItems(Map<String, String> configItems);
    void insertConfigItem(String key, String value);
    Map getApplicationConfigurationByNamespace(String namespace);

}