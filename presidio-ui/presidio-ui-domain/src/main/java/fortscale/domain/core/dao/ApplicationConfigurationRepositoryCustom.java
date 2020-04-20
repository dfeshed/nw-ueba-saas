package fortscale.domain.core.dao;

import java.util.Map;

public interface ApplicationConfigurationRepositoryCustom {

    void updateConfigItems(Map<String, String> configItems);
    void insertConfigItems(Map<String, String> configItems);
    void insertConfigItem(String key, String value);

}