package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.EntityType;
import fortscale.services.configuration.Impl.InitPartConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
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

    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        ConfigurationParam dataSourceName = configurationParams.get("dataSourceName");
        ConfigurationParam dataSourceType = configurationParams.get("dataSourceType");
        ConfigurationParam dataSourceLists = configurationParams.get("dataSourceLists");

        currGDSConfigurationState.setDataSourceName(dataSourceName.getParamValue());
        currGDSConfigurationState.setEntityType(EntityType.valueOf(dataSourceType.getParamValue().toUpperCase()));
        currGDSConfigurationState.setExistingDataSources(dataSourceLists.getParamValue());

        GDSSchemaDefinitionState GDSSchemaDefinitionState = currGDSConfigurationState.getGDSSchemaDefinitionState();
        Boolean sourceIpFlag = configurationParams.get("sourceIpFlag").getParamFlag();
        GDSSchemaDefinitionState.setHasSourceIp(sourceIpFlag);

        Boolean targetIpFlag = configurationParams.get("targetIpFlag").getParamFlag();
        GDSSchemaDefinitionState.setHasTargetIp(targetIpFlag);

        String dataFields = configurationParams.get("dataFields").getParamValue();
        GDSSchemaDefinitionState.setDataFields(dataFields);

        String enrichFields = configurationParams.get("enrichFields").getParamValue();
        GDSSchemaDefinitionState.setEnrichFields(enrichFields);

        String enrichDelimiter = configurationParams.get("enrichDelimiter").getParamValue();
        GDSSchemaDefinitionState.setEnrichDelimiter(enrichDelimiter);

        String enrichTableName = configurationParams.get("enrichTableName").getParamValue();
        GDSSchemaDefinitionState.setEnrichTableName(enrichTableName);

        String scoreFields = configurationParams.get("scoreFields").getParamValue();
        GDSSchemaDefinitionState.setScoreFields(scoreFields);

        String scoreDelimiter = configurationParams.get("scoreDelimiter").getParamValue();
        GDSSchemaDefinitionState.setScoreDelimiter(scoreDelimiter);

        String scoreTableName = configurationParams.get("scoreTableName").getParamValue();
        GDSSchemaDefinitionState.setScoreTableName(scoreTableName);

        Boolean topSchemaFlag = configurationParams.get("topSchemaFlag").getParamFlag();
        GDSSchemaDefinitionState.setHasTopSchema(topSchemaFlag);

        // TODO how do we get it?
//        Boolean normalizedUserNameField = configurationParams.get("normalizedUserNameField").getParamFlag();
//        GDSSchemaDefinitionState.setHasNormalizedUserNameField(normalizedUserNameField);

        String dataDelimiter = configurationParams.get("dataDelimiter").getParamValue();
        GDSSchemaDefinitionState.setDataDelimiter(dataDelimiter);

        String dataTableName = configurationParams.get("dataTableName").getParamValue();
        GDSSchemaDefinitionState.setDataTableName(dataTableName);

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
        // we suppose this will impact other configuration steps as well, therefor in this case of schema reset
        // we will actually reset all configuration definitions
        currGDSConfigurationState.reset();
    }
}
