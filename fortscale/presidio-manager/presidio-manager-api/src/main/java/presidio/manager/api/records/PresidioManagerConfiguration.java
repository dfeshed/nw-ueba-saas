package presidio.manager.api.records;


public class PresidioManagerConfiguration {

    private DataPipeLineConfiguration dataPipeLineConfiguration;

    private PresidioSystemConfiguration systemConfiguration;

    public PresidioManagerConfiguration(DataPipeLineConfiguration dataPipeLineConfiguration, PresidioSystemConfiguration systemConfiguration) {
        this.dataPipeLineConfiguration = dataPipeLineConfiguration;
        this.systemConfiguration = systemConfiguration;
    }

    public DataPipeLineConfiguration getDataPipeLineConfiguration() {
        return dataPipeLineConfiguration;
    }

    public PresidioSystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }
}
