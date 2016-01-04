package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;

import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public class GDSEnrichmentCLIPopulator implements GDSConfigurationPopulator{

    private GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    @Override
    public Map<String, ConfigurationParam> populateConfigurationData(GDSConfigurationStateImpl currentConfigurationState) throws Exception {




    }
}
