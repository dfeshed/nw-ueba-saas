package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.EntityType;
import fortscale.services.configuration.Impl.InitPartConfiguration;
import fortscale.services.configuration.gds.state.GDSSchemaDefinitionState;

import java.util.Map;

/**
 * Schema configurator implementation (HDFS paths and impala tables)
 *
 * @author gils
 * 30/12/2015
 */
public class GDSSchemaConfigurator extends GDSBaseConfigurator {

    public GDSSchemaConfigurator() {
        configurationService = new InitPartConfiguration();
    }

    public void configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        configureBaseDefinitions(configurationParams);

        configureStreamingTopologyDefinitions(configurationParams);

        GDSSchemaDefinitionState schemaDefinitionState = currGDSConfigurationState.getGDSSchemaDefinitionState();
        boolean sourceIpFlag = configurationParams.get("sourceIpFlag").getParamFlag();
        schemaDefinitionState.setHasSourceIp(sourceIpFlag);

        boolean targetIpFlag = configurationParams.get("targetIpFlag").getParamFlag();
        schemaDefinitionState.setHasTargetIp(targetIpFlag);

        String dataFields = configurationParams.get("dataFields").getParamValue();
        schemaDefinitionState.setDataFields(dataFields);

        String enrichFields = configurationParams.get("enrichFields").getParamValue();
        schemaDefinitionState.setEnrichFields(enrichFields);

        String enrichDelimiter = configurationParams.get("enrichDelimiter").getParamValue();
        schemaDefinitionState.setEnrichDelimiter(enrichDelimiter);

        String enrichTableName = configurationParams.get("enrichTableName").getParamValue();
        schemaDefinitionState.setEnrichTableName(enrichTableName);

        String scoreFields = configurationParams.get("scoreFields").getParamValue();
        schemaDefinitionState.setScoreFields(scoreFields);

        String scoreDelimiter = configurationParams.get("scoreDelimiter").getParamValue();
        schemaDefinitionState.setScoreDelimiter(scoreDelimiter);

        String scoreTableName = configurationParams.get("scoreTableName").getParamValue();
        schemaDefinitionState.setScoreTableName(scoreTableName);

        Boolean topSchemaFlag = configurationParams.get("topSchemaFlag").getParamFlag();
        schemaDefinitionState.setHasTopSchema(topSchemaFlag);

        // TODO how do we get it?
//        Boolean normalizedUserNameField = configurationParams.get("normalizedUserNameField").getParamFlag();
//        schemaDefinitionState.setHasNormalizedUserNameField(normalizedUserNameField);

        String dataDelimiter = configurationParams.get("dataDelimiter").getParamValue();
        schemaDefinitionState.setDataDelimiter(dataDelimiter);

        String dataTableName = configurationParams.get("dataTableName").getParamValue();
        schemaDefinitionState.setDataTableName(dataTableName);

        configurationService.setGDSConfigurationState(currGDSConfigurationState);
    }

    private void configureBaseDefinitions(Map<String, ConfigurationParam> configurationParams) {
        ConfigurationParam dataSourceName = configurationParams.get("dataSourceName");
        ConfigurationParam dataSourceType = configurationParams.get("dataSourceType");
        ConfigurationParam dataSourceLists = configurationParams.get("dataSourceLists");

        currGDSConfigurationState.setDataSourceName(dataSourceName.getParamValue());
        currGDSConfigurationState.setEntityType(EntityType.valueOf(dataSourceType.getParamValue().toUpperCase()));
        currGDSConfigurationState.setExistingDataSources(dataSourceLists.getParamValue());
    }

    private void configureStreamingTopologyDefinitions(Map<String, ConfigurationParam> configurationParams) {
        boolean sourceIpResolvingFlag = configurationParams.get("sourceIpResolvingFlag").getParamFlag();
        currGDSConfigurationState.setSourceIpResolvingRequired(sourceIpResolvingFlag);
        boolean targetIpResolvingFlag = configurationParams.get("targetIpResolvingFlag").getParamFlag();
        currGDSConfigurationState.setTargetIpResolvingRequired(targetIpResolvingFlag);
        boolean sourceMachineNormalizationFlag = configurationParams.get("sourceMachineNormalizationFlag").getParamFlag();
        currGDSConfigurationState.setSourceMachineNormalizationRequired(sourceMachineNormalizationFlag);
        boolean targetMachineNormalizationFlag = configurationParams.get("targetMachineNormalizationFlag").getParamFlag();
        currGDSConfigurationState.setTargetMachineNormalizationRequired(targetMachineNormalizationFlag);
        boolean sourceIpGeoLocationFlag = configurationParams.get("sourceIpGeoLocationFlag").getParamFlag();
        currGDSConfigurationState.setSourceIpGeoLocationRequired(sourceIpGeoLocationFlag);
        boolean targetIpGeoLocationFlag = configurationParams.get("targetIpGeoLocationFlag").getParamFlag();
        currGDSConfigurationState.setTargetIpGeoLocationRequired(targetIpGeoLocationFlag);
    }

    @Override
    public void reset() throws Exception {
        // we suppose this will impact other configuration steps as well, therefor in this case of schema reset
        // we will actually reset all configuration definitions
        currGDSConfigurationState.reset();
    }
}
