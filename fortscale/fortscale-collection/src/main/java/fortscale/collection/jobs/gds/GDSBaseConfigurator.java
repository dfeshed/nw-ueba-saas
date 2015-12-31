package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.gds.state.GDSConfigurationState;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author gils
 * 30/12/2015
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    protected GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    protected GDSConfigurationState gdsConfigurationState;

    @Value("${fortscale.data.source}")
    private String currentDataSources;

    public GDSBaseConfigurator(GDSConfigurationState gdsConfigurationState) {
        this.gdsConfigurationState = gdsConfigurationState;
    }

    public void configure() throws Exception {
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
