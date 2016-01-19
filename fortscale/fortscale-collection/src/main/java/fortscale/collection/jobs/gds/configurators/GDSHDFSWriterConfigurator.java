package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.HDFSWriteTaskConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * HDFS Writer configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSHDFSWriterConfigurator extends GDSBaseConfigurator {

    private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topics";

    public GDSHDFSWriterConfigurator() {
        configurationWriterService = new HDFSWriteTaskConfigurationWriter();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
        ConfigurationParam taskName = paramsMap.get(TASK_NAME_PARAM);
        ConfigurationParam outputTopic = paramsMap.get(OUTPUT_TOPIC_PARAM);


        ConfigurationParam fieldList = paramsMap.get("fieldList");
        ConfigurationParam delimiter = paramsMap.get("delimiter");
        ConfigurationParam hdfsPath = paramsMap.get("hdfsPath");
        ConfigurationParam fileName = paramsMap.get("fileName");
        ConfigurationParam tableName = paramsMap.get("tableName");
        ConfigurationParam partitionStrategy = paramsMap.get("partitionStrategy");
        ConfigurationParam discriminatorsFields = paramsMap.get("discriminatorsFields");

        GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState = currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState();

        hdfsWriterState.setTaskName(taskName.getParamValue());
        hdfsWriterState.setLastState(lastState);
        hdfsWriterState.setOutputTopic(outputTopic.getParamValue());
        hdfsWriterState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);

        hdfsWriterState.setFieldList(fieldList.getParamValue());
        hdfsWriterState.setDelimiter(delimiter.getParamValue());
        hdfsWriterState.setHdfsPath(hdfsPath.getParamValue());
        hdfsWriterState.setFileName(fileName.getParamValue());
        hdfsWriterState.setTableName(tableName.getParamValue());
        hdfsWriterState.setPartitionStrategy(partitionStrategy.getParamValue());
        hdfsWriterState.setDiscriminatorsFields(discriminatorsFields.getParamValue());

        String lastStaeClac = taskName.getParamValue();
        if (lastStaeClac.indexOf("_") != -1 )
            lastStaeClac = lastStaeClac.substring(0,lastStaeClac.indexOf("_")-1);

        currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(lastStaeClac);

    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState().reset();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.HDFS_WRITER;
    }
}

