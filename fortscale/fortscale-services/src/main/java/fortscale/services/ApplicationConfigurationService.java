package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface ApplicationConfigurationService {

    List<ApplicationConfiguration> getApplicationConfiguration ();

    void updateConfigItems (Map<String, String> configItems);
}
