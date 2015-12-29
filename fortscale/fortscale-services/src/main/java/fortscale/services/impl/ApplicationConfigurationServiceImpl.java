package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.dao.ApplicationConfigurationRepositoryImpl;
import fortscale.services.ApplicationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService {

    @Autowired
    private ApplicationConfigurationRepositoryImpl applicationConfigurationRepository;

    /**
     * Returns a list of ApplicationConfiguration documents
     *
     * @return List<ApplicationConfiguration>
     */
    @Override
    public List<ApplicationConfiguration> getApplicationConfiguration() {
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<>();
        applicationConfigurationList.addAll(applicationConfigurationRepository.findAll());
        return applicationConfigurationList;
    }

    /**
     * Updates or creates config items.
     *
     * @param configItems A map of config items.
     */
    @Override
    public void updateConfigItems (Map<String, String> configItems) {
        applicationConfigurationRepository.updateConfigItems(configItems);
    }
}
