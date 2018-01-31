package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PresidioManagerConfiguration {
    public static final String SYSTEM = "system";
    public static final String DATA_PIPE_LINE = "dataPipeline";
    public static final String START_TIME = "startTime";
    public static final String FORWARDER = "forwarder";

    @JsonProperty(DATA_PIPE_LINE)
    private DataPipeLineConfiguration dataPipeLineConfiguration;

    @JsonProperty(SYSTEM)
    private PresidioSystemConfiguration systemConfiguration;

    @JsonProperty(FORWARDER)
    private OutputConfiguration outputConfiguration;

    public PresidioManagerConfiguration() {
    }

    public PresidioManagerConfiguration(DataPipeLineConfiguration dataPipeLineConfiguration,
                                        PresidioSystemConfiguration systemConfiguration,
                                        OutputConfiguration outputConfiguration) {
        this.dataPipeLineConfiguration = dataPipeLineConfiguration;
        this.systemConfiguration = systemConfiguration;
        this.outputConfiguration = outputConfiguration;
    }

    public OutputConfiguration getOutputConfiguration() {
        return outputConfiguration;
    }

    public DataPipeLineConfiguration getDataPipeLineConfiguration() {
        return dataPipeLineConfiguration;
    }

    public PresidioSystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }
}
