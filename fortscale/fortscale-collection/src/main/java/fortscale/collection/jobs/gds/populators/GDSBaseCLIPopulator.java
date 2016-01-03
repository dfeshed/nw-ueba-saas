package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author gils
 * 03/01/2016
 */
abstract class GDSBaseCLIPopulator implements GDSConfigurationPopulator {

    protected GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    @Value("${fortscale.data.source}")
    private String currentDataSources = "ssh,vpn"; // TODO fix

}
