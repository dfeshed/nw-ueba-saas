package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PresidioManagerConfiguration {
    public static final String SYSTEM = "system";
    public static final String DATA_PIPE_LINE = "dataPipeline";
    public static final String START_TIME = "startTime";

    @JsonProperty(DATA_PIPE_LINE)
    private DataPipeLineConfiguration dataPipeLineConfiguration;

    @JsonProperty(SYSTEM)
    private PresidioSystemConfiguration systemConfiguration;

    public PresidioManagerConfiguration() {
    }

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
