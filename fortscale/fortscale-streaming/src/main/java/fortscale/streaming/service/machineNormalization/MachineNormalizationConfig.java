package fortscale.streaming.service.machineNormalization;


import fortscale.streaming.service.StreamingTaskConfig;

import java.util.List;

public class MachineNormalizationConfig implements StreamingTaskConfig {
    private String dataSource;
    private String lastState;
    private String outputTopic;
    private String partitionField;
    List<MachineNormalizationFieldsConfig> machineNormalizationFieldsConfigs;

    public MachineNormalizationConfig(String dataSource, String lastState, String outputTopic, String partitionField, List<MachineNormalizationFieldsConfig> machineNormalizationFieldsConfigs) {
        setDataSource(dataSource);
        setLastState(lastState);
        setOutputTopic(outputTopic);
        setPartitionField(partitionField);
        setMachineNormalizationFieldsConfigs(machineNormalizationFieldsConfigs);
    }

    public String getDataSource() {return dataSource;}

    public void setDataSource(String dataSource) {this.dataSource = dataSource;}

    public String getLastState() {return lastState;}

    public void setLastState(String lastState) {this.lastState = lastState;}

    @Override
    public String getOutputTopic() {return outputTopic;}

    public void setOutputTopic(String outputTopic) {this.outputTopic = outputTopic;}

    @Override
    public String getPartitionField() {return partitionField;}

    public void setPartitionField(String partitionField) {this.partitionField = partitionField;}

    public List<MachineNormalizationFieldsConfig> getMachineNormalizationFieldsConfigs() {return machineNormalizationFieldsConfigs;}

    public void setMachineNormalizationFieldsConfigs(List<MachineNormalizationFieldsConfig> machineNormalizationFieldsConfigs) {this.machineNormalizationFieldsConfigs = machineNormalizationFieldsConfigs;}
}
