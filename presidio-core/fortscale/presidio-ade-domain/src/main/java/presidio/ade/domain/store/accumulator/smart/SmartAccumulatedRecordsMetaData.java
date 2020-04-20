package presidio.ade.domain.store.accumulator.smart;

public class SmartAccumulatedRecordsMetaData {
    private String configurationName;

    public SmartAccumulatedRecordsMetaData(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getConfigurationName() {
        return configurationName;
    }
}
