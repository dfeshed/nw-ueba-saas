package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepositoryImpl;
import fortscale.services.ApplicationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {

    @Autowired
    private ApplicationConfigurationRepositoryImpl applicationConfigurationRepository;

    @Override
    public List<ApplicationConfiguration> getApplicationConfiguration() {
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<>();
        applicationConfigurationList.addAll(applicationConfigurationRepository.findAll());
        return applicationConfigurationList;
    }

    /**
     *
     * @param configItems A map of config items.
     */
    @Override
    public void updateConfigItems (HashMap<String, String> configItems) {
        applicationConfigurationRepository.updateConfigItems(configItems);
    }
}
