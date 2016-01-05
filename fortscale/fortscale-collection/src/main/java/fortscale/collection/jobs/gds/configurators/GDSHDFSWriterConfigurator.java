package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.HDFSWriteTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
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
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState = currGDSConfigurationState.getGDSEnrichmentDefinitionState().getHdfsWriterState();

        ConfigurationParam fieldList = configurationParams.get("fieldList");
        ConfigurationParam delimiter = configurationParams.get("delimiter");
        ConfigurationParam hdfsPath = configurationParams.get("hdfsPath");
        ConfigurationParam fileName = configurationParams.get("fileName");
        ConfigurationParam tableName = configurationParams.get("tableName");
        ConfigurationParam partitionStrategy = configurationParams.get("partitionStrategy");
        ConfigurationParam discriminatorsFields = configurationParams.get("discriminatorsFields");

        hdfsWriterState.setFieldList(fieldList.getParamValue());
        hdfsWriterState.setDelimiter(delimiter.getParamValue());
        hdfsWriterState.setHdfsPath(hdfsPath.getParamValue());
        hdfsWriterState.setFileName(fileName.getParamValue());
        hdfsWriterState.setTableName(tableName.getParamValue());
        hdfsWriterState.setPartitionStrategy(partitionStrategy.getParamValue());
        hdfsWriterState.setDiscriminatorsFields(discriminatorsFields.getParamValue());

        configurationService.setGDSConfigurationState(currGDSConfigurationState);

        return currGDSConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (configurationService.init()) {
            configurationService.applyConfiguration();
        }

        configurationService.done();
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getGDSEnrichmentDefinitionState().getHdfsWriterState().reset();
    }
}

