package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.collection.jobs.gds.input.populators.enrichment.GDSHDFSWriterTableNamesEnum;
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

        confiureHDFSWriterState(configurationParams.get(GDSHDFSWriterTableNamesEnum.ENRICH.name()),currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterEnrichedState());
        confiureHDFSWriterState(configurationParams.get(GDSHDFSWriterTableNamesEnum.SCORE.name()),currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterScoreState());
        if(currGDSConfigurationState.getSchemaDefinitionState().hasTopSchema()){
            confiureHDFSWriterState(configurationParams.get(GDSHDFSWriterTableNamesEnum.TOP_SCORE.name()),currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterTopScoreState());
        }

    }

    private void confiureHDFSWriterState(Map<String, ConfigurationParam> paramsMap,GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState ){

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
        ConfigurationParam levelDBSuffix = paramsMap.get("levelDBSuffixParam");

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
        hdfsWriterState.setLevelDBSuffix(levelDBSuffix.getParamValue());

        currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(""); //TODO fix
    }


    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterEnrichedState().reset();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.HDFS_WRITER;
    }
}

