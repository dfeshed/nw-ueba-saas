package fortscale.collection.jobs.gds;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author gils
 * 30/12/2015
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    private static final String DATA_SOURCE_NAME = "dataSourceName";
    private static final String DATA_SOURCE_TYPE = "dataSourceType";

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
            String dataSourceName = gdsInputHandler.getInput(DATA_SOURCE_NAME);

            gdsConfigurationState.setDataSourceName(dataSourceName);

            GDSMenuPrintHelper.printDataSourceTypeMenuOptions(dataSourceName);
            String dataSourceType = gdsInputHandler.getInput(DATA_SOURCE_TYPE);

            gdsConfigurationState.setEntityType(GDSEntityType.valueOf(dataSourceType.toUpperCase()));

            gdsConfigurationState.setCurrentDataSources(currentDataSources);
        }
    }
}
