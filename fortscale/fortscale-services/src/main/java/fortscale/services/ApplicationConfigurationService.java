package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;

import java.util.HashMap;
import java.util.List;


public interface ApplicationConfigurationService {

    public List<ApplicationConfiguration> getApplicationConfiguration ();

    public void updateConfigItems (HashMap<String, String> configItems);
}
