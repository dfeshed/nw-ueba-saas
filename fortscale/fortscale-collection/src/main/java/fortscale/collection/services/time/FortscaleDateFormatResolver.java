package fortscale.collection.services.time;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gils
 * 03/03/2016
 */
public class FortscaleDateFormatResolver implements InitializingBean{

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ApplicationConfiguration> applicationConfiguration = applicationConfigurationService.getApplicationConfiguration();
        LinkedList<String> availableInputFormats = FortscaleTimeFormats.getAvailableInputFormats();
        String x = null;
    }
}
