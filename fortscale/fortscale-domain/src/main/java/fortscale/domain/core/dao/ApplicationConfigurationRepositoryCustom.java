package fortscale.domain.core.dao;

import com.mongodb.WriteResult;
import fortscale.domain.core.ApplicationConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationConfigurationRepositoryCustom {

    List<ApplicationConfiguration> findAll();

    void updateConfigItems(Map<String, String> configItems);
}
