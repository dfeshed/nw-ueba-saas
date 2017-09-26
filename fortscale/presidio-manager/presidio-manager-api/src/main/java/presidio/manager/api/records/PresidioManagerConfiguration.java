package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PresidioManagerConfiguration {

    @JsonProperty("dataPipeline")
    private DataPipeLineConfiguration dataPipeLineConfiguration;

    @JsonProperty("system")
    private PresidioSystemConfiguration systemConfiguration;

    public PresidioManagerConfiguration() {}

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
