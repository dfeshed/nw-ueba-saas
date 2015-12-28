package fortscale.domain.core.dao;

import com.mongodb.WriteResult;
import fortscale.domain.core.ApplicationConfiguration;
import java.util.HashMap;
import java.util.List;

public interface ApplicationConfigurationRepositoryCustom {

    public List<ApplicationConfiguration> findAll();

    public void updateConfigItems(HashMap<String, String> configItems);
}
