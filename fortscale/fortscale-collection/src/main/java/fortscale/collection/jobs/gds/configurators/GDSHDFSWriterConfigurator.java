package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.HDFSWriteTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * HDFS Writer configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSHDFSWriterConfigurator extends GDSBaseConfigurator {

    public GDSHDFSWriterConfigurator() {
        configurationService = new HDFSWriteTaskConfiguration();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        ConfigurationParam fieldList = paramsMap.get("fieldList");
        ConfigurationParam delimiter = paramsMap.get("delimiter");
        ConfigurationParam hdfsPath = paramsMap.get("hdfsPath");
        ConfigurationParam fileName = paramsMap.get("fileName");
        ConfigurationParam tableName = paramsMap.get("tableName");
        ConfigurationParam partitionStrategy = paramsMap.get("partitionStrategy");
        ConfigurationParam discriminatorsFields = paramsMap.get("discriminatorsFields");

        GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState = currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState();

        hdfsWriterState.setFieldList(fieldList.getParamValue());
        hdfsWriterState.setDelimiter(delimiter.getParamValue());
        hdfsWriterState.setHdfsPath(hdfsPath.getParamValue());
        hdfsWriterState.setFileName(fileName.getParamValue());
        hdfsWriterState.setTableName(tableName.getParamValue());
        hdfsWriterState.setPartitionStrategy(partitionStrategy.getParamValue());
        hdfsWriterState.setDiscriminatorsFields(discriminatorsFields.getParamValue());
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState().reset();
    }
}

