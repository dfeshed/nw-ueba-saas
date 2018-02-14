package presidio.manager.api.records;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PresidioManagerConfiguration {
    public static final String SYSTEM = "system";
    public static final String DATA_PIPE_LINE = "dataPipeline";
    public static final String START_TIME = "startTime";
    public static final String OUTPUT_FORWARDING = "outputForwarding";

    @JsonProperty(DATA_PIPE_LINE)
    private DataPipeLineConfiguration dataPipeLineConfiguration;

    @JsonProperty(SYSTEM)
    private PresidioSystemConfiguration systemConfiguration;

    @JsonProperty(OUTPUT_FORWARDING)
    private OutputConfigurationCreator outputConfigurationCreator;

    public PresidioManagerConfiguration() {
    }

    public PresidioManagerConfiguration(DataPipeLineConfiguration dataPipeLineConfiguration,
                                        PresidioSystemConfiguration systemConfiguration,
                                        OutputConfigurationCreator outputConfigurationCreator) {
        this.dataPipeLineConfiguration = dataPipeLineConfiguration;
        this.systemConfiguration = systemConfiguration;
        this.outputConfigurationCreator = outputConfigurationCreator;
    }

    public OutputConfigurationCreator getOutputConfigurationCreator() {
        return outputConfigurationCreator;
    }

    public DataPipeLineConfiguration getDataPipeLineConfiguration() {
        return dataPipeLineConfiguration;
    }

    public PresidioSystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }
}
