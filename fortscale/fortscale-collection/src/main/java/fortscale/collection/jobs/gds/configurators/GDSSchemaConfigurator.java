package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.InitPartConfiguration;
import fortscale.services.configuration.state.GDSConfigurationStateImpl;
import fortscale.services.configuration.state.GDSEntityType;
import fortscale.services.configuration.state.SchemaDefinitionState;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * Configure the Init configuration - The part that support the schema (HDFS paths and impala tables)
 *
 * @author gils
 * 30/12/2015
 */
public class GDSSchemaConfigurator implements GDSConfigurator {

    private static Logger logger = Logger.getLogger(GDSSchemaConfigurator.class);

    private GDSConfigurationStateImpl gdsConfigurationState = new GDSConfigurationStateImpl();

    private ConfigurationService initConfigurationService = new InitPartConfiguration();

    public GDSConfigurationStateImpl configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        ConfigurationParam dataSourceName = configurationParams.get("dataSourceName");
        ConfigurationParam dataSourceType = configurationParams.get("dataSourceType");
        ConfigurationParam dataSourceLists = configurationParams.get("dataSourceLists");

        gdsConfigurationState.setDataSourceName(dataSourceName.getParamValue());
        gdsConfigurationState.setEntityType(GDSEntityType.valueOf(dataSourceType.getParamValue().toUpperCase()));
        gdsConfigurationState.setExistingDataSources(dataSourceLists.getParamValue());

        SchemaDefinitionState schemaDefinitionState = gdsConfigurationState.getSchemaDefinitionState();
        Boolean sourceIpFlag = configurationParams.get("sourceIpFlag").getParamFlag();
        schemaDefinitionState.setHasSourceIp(sourceIpFlag);

        Boolean targetIpFlag = configurationParams.get("targetIpFlag").getParamFlag();
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

        initConfigurationService.setGDSConfigurationState(gdsConfigurationState);
        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (initConfigurationService.init()) {
            initConfigurationService.applyConfiguration();
        }

        initConfigurationService.done();
    }

    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}
