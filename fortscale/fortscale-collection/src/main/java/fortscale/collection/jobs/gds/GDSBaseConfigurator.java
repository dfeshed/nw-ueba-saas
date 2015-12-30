package fortscale.collection.jobs.gds;

/**
 * @author gils
 * 30/12/2015
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    private static final String DATA_SOURCE_NAME = "dataSourceName";
    private static final String DATA_SOURCE_TYPE = "dataSourceType";

    protected GDSInputHandler gdsInputHandler = new GDSStandardInputHandler();

    protected GDSConfigurationState gdsConfigurationState;

    public GDSBaseConfigurator(GDSConfigurationState gdsConfigurationState) {
        this.gdsConfigurationState = new GDSConfigurationState();
    }

    public void configure() throws Exception {
        System.out.println("Please enter the new data source name: ");
        String dataSourceName = gdsInputHandler.getInput(DATA_SOURCE_NAME);

        gdsConfigurationState.setDataSourceName(dataSourceName);

        GDSMenuPrintHelper.printDataSourceTypeMenuOptions(dataSourceName);
        String dataSourceType = gdsInputHandler.getInput(DATA_SOURCE_TYPE);

        gdsConfigurationState.setEntityType(GDSEntityType.valueOf(dataSourceType.toUpperCase()));
    }
}
