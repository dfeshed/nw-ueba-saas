package fortscale.collection.jobs.gds.populators;

import fortscale.collection.jobs.gds.GDSEntityType;
import fortscale.collection.jobs.gds.GDSInputHandler;
import fortscale.collection.jobs.gds.GDSMenuPrinterHelper;
import fortscale.collection.jobs.gds.GDSStandardInputHandler;
import fortscale.collection.jobs.gds.state.GDSConfigurationState;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author gils
 * 03/01/2016
 */
abstract class GDSBaseCLIPopulator implements GDSUserConfigurationPopulator {

    protected GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    protected GDSConfigurationState gdsConfigurationState;

    @Value("${fortscale.data.source}")
    private String currentDataSources;

    public GDSBaseCLIPopulator(GDSConfigurationState gdsConfigurationState) {
        this.gdsConfigurationState = gdsConfigurationState;
    }

    @Override
    public void populateConfigurationData() throws Exception {
        if (!gdsConfigurationState.isDataSourceAlreadyDefined()) {
            System.out.println("Please enter the data source name: ");
            String dataSourceName = gdsInputHandler.getInput();

            gdsConfigurationState.setDataSourceName(dataSourceName);

            GDSMenuPrinterHelper.printDataSourceTypeMenuOptions(dataSourceName);
            String dataSourceType = gdsInputHandler.getInput();

            gdsConfigurationState.setEntityType(GDSEntityType.valueOf(dataSourceType.toUpperCase()));

            gdsConfigurationState.setCurrentDataSources(currentDataSources);
        }
    }
}
