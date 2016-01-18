package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.EntityType;
import fortscale.services.configuration.Impl.SchemaDefinitionConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSSchemaDefinitionState;
import fortscale.services.configuration.gds.state.field.FieldMetadata;
import fortscale.services.configuration.gds.state.field.FieldMetadataDictionary;
import fortscale.services.configuration.gds.state.field.FieldType;
import fortscale.services.configuration.gds.state.field.ScoreFieldMetadata;
import fortscale.utils.ConversionUtils;

import java.util.Map;
import java.util.Set;

/**
 * Schema configurator implementation (HDFS paths and impala tables)
 *
 * @author gils
 * 30/12/2015
 */
public class GDSSchemaConfigurator extends GDSBaseConfigurator {

    public GDSSchemaConfigurator() {
        configurationWriterService = new SchemaDefinitionConfigurationWriter();
    }

    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        configureBaseDefinitions(paramsMap);

        configureStreamingTopologyDefinitions(paramsMap);

        GDSSchemaDefinitionState schemaDefinitionState = currGDSConfigurationState.getSchemaDefinitionState();
        boolean sourceIpFlag = paramsMap.get("sourceIpFlag").getParamFlag();
        schemaDefinitionState.setHasSourceIp(sourceIpFlag);

        boolean targetIpFlag = paramsMap.get("targetIpFlag").getParamFlag();
        schemaDefinitionState.setHasTargetIp(targetIpFlag);

        String dataTableFields = paramsMap.get("dataTableFields").getParamValue();
        schemaDefinitionState.setDataTableFields(dataTableFields);

        String enrichTableFields = paramsMap.get("enrichTableFields").getParamValue();
        schemaDefinitionState.setEnrichTableFields(enrichTableFields);

        String enrichDelimiter = paramsMap.get("enrichDelimiter").getParamValue();
        schemaDefinitionState.setEnrichDelimiter(enrichDelimiter);

        String enrichTableName = paramsMap.get("enrichTableName").getParamValue();
        schemaDefinitionState.setEnrichTableName(enrichTableName);

        String scoreTableFields = paramsMap.get("scoreTableFields").getParamValue();
        schemaDefinitionState.setScoreTableFields(scoreTableFields);

        String scoreDelimiter = paramsMap.get("scoreDelimiter").getParamValue();
        schemaDefinitionState.setScoreDelimiter(scoreDelimiter);

        String scoreTableName = paramsMap.get("scoreTableName").getParamValue();
        schemaDefinitionState.setScoreTableName(scoreTableName);

        Boolean topSchemaFlag = paramsMap.get("topSchemaFlag").getParamFlag();
        schemaDefinitionState.setHasTopSchema(topSchemaFlag);

        String normalizedUserNameField = paramsMap.get("normalizedUserNameField").getParamValue();
        schemaDefinitionState.setNormalizedUserNameField(normalizedUserNameField);

        String dataDelimiter = paramsMap.get("dataDelimiter").getParamValue();
        schemaDefinitionState.setDataDelimiter(dataDelimiter);

        String dataTableName = paramsMap.get("dataTableName").getParamValue();
        schemaDefinitionState.setDataTableName(dataTableName);

        configureBaseFields(paramsMap);
        configureAdditionalFields(paramsMap);
    }

    private void configureBaseFields(Map<String, ConfigurationParam> paramsMap) {
        String baseFieldsCSV = paramsMap.get("baseFieldsCSV").getParamValue();

        FieldMetadataDictionary fieldMetadataDictionary = currGDSConfigurationState.getSchemaDefinitionState().getFieldMetadataDictionary();

        Map<String, String> baseFieldToTypeMap = ConversionUtils.convertCSVToMap(baseFieldsCSV);

        for (Map.Entry<String, String> baseFieldToTypeEntry : baseFieldToTypeMap.entrySet()) {
            String baseFieldName = baseFieldToTypeEntry.getKey();
            String baseFieldType = baseFieldToTypeEntry.getValue();

            FieldMetadata fieldMetadata = new FieldMetadata(baseFieldName, FieldType.valueOf(baseFieldType.toUpperCase()), false);

            fieldMetadataDictionary.addField(fieldMetadata);
        }

        String baseScoreFieldsCSV = paramsMap.get("baseScoreFieldsCSV").getParamValue();
        String baseScoreFieldToFieldNameCSV = paramsMap.get("baseScoreFieldToFieldNameCSV").getParamValue();
        String populatedBaseScoreFieldsCSV = paramsMap.get("populatedBaseScoreFieldsCSV").getParamValue();

        Map<String, String> baseScoreFieldToTypeMap = ConversionUtils.convertCSVToMap(baseScoreFieldsCSV);

        Map<String, String> baseScoreFieldToFieldNameMap = ConversionUtils.convertCSVToMap(baseScoreFieldToFieldNameCSV);

        Set<String> populatedBaseScoreFieldsSet = ConversionUtils.convertCSVToSet(populatedBaseScoreFieldsCSV);

        for (Map.Entry<String, String> baseScoreFieldToTypeEntry : baseScoreFieldToTypeMap.entrySet()) {
            String baseScoreFieldName = baseScoreFieldToTypeEntry.getKey();

            boolean isInUse = populatedBaseScoreFieldsSet.contains(baseScoreFieldName);
            ScoreFieldMetadata baseScoreFieldMetadata = new ScoreFieldMetadata(baseScoreFieldName, isInUse, false);

            fieldMetadataDictionary.addScoreField(baseScoreFieldMetadata);

            if (baseScoreFieldToFieldNameMap.containsKey(baseScoreFieldName)) {
                String baseFieldName = baseScoreFieldToFieldNameMap.get(baseScoreFieldName);
                fieldMetadataDictionary.pairFieldToScore(baseFieldName, baseScoreFieldName);
            }
        }
    }

    private void configureAdditionalFields(Map<String, ConfigurationParam> paramsMap) {
        String additionalFieldsCSV = paramsMap.get("additionalFieldsCSV").getParamValue();

        String additionalScoreFieldsCSV = paramsMap.get("additionalScoreFieldsCSV").getParamValue();

        String additionalScoreFieldToFieldNameCSV = paramsMap.get("additionalScoreFieldToFieldNameCSV").getParamValue();

        FieldMetadataDictionary fieldMetadataDictionary = currGDSConfigurationState.getSchemaDefinitionState().getFieldMetadataDictionary();

        Map<String, String> additionalFieldToTypeMap = ConversionUtils.convertCSVToMap(additionalFieldsCSV);

        for (Map.Entry<String, String> additionalFieldToTypeEntry : additionalFieldToTypeMap.entrySet()) {
            String additionalFieldName = additionalFieldToTypeEntry.getKey();
            String additionalFieldType = additionalFieldToTypeEntry.getValue();

            FieldMetadata fieldMetadata = new FieldMetadata(additionalFieldName, FieldType.valueOf(additionalFieldType.toUpperCase()), true);

            fieldMetadataDictionary.addField(fieldMetadata);
        }

        Map<String, String> additionalScoreFieldNameToTypeMap = ConversionUtils.convertCSVToMap(additionalScoreFieldsCSV);
        Map<String, String> additionalScoreFieldNameToFieldMap = ConversionUtils.convertCSVToMap(additionalScoreFieldToFieldNameCSV);

        for (Map.Entry<String, String> scoreFieldToType : additionalScoreFieldNameToTypeMap.entrySet()) {
            String scoreFieldName = scoreFieldToType.getKey();

            ScoreFieldMetadata scoreFieldMetadata = new ScoreFieldMetadata(scoreFieldName, true, true);

            fieldMetadataDictionary.addScoreField(scoreFieldMetadata);

            if (additionalScoreFieldNameToFieldMap.containsKey(scoreFieldName)) {
                String fieldName = additionalScoreFieldNameToFieldMap.get(scoreFieldName);
                fieldMetadataDictionary.pairFieldToScore(fieldName, scoreFieldName);
            }
        }

    }

    private void configureBaseDefinitions(Map<String, ConfigurationParam> configurationParams) {
        ConfigurationParam dataSourceName = configurationParams.get("dataSourceName");
        ConfigurationParam dataSourceType = configurationParams.get("dataSourceType");
        ConfigurationParam dataSourceLists = configurationParams.get("dataSourceLists");

        currGDSConfigurationState.setDataSourceName(dataSourceName.getParamValue());
        currGDSConfigurationState.setEntityType(EntityType.valueOf(dataSourceType.getParamValue().toUpperCase()));
        currGDSConfigurationState.setExistingDataSources(dataSourceLists.getParamValue());
        currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue("etl");
    }

    private void configureStreamingTopologyDefinitions(Map<String, ConfigurationParam> configurationParams) {
        if (configurationParams.containsKey("sourceIpResolvingFlag")) {
            boolean sourceIpResolvingFlag = configurationParams.get("sourceIpResolvingFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setSourceIpResolvingRequired(sourceIpResolvingFlag);
        }
        if (configurationParams.containsKey("targetIpResolvingFlag")) {
            boolean targetIpResolvingFlag = configurationParams.get("targetIpResolvingFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setTargetIpResolvingRequired(targetIpResolvingFlag);
        }

        if (configurationParams.containsKey("sourceMachineNormalizationFlag")) {
            boolean sourceMachineNormalizationFlag = configurationParams.get("sourceMachineNormalizationFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setSourceMachineNormalizationRequired(sourceMachineNormalizationFlag);
        }
        if (configurationParams.containsKey("targetMachineNormalizationFlag")) {
            boolean targetMachineNormalizationFlag = configurationParams.get("targetMachineNormalizationFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setTargetMachineNormalizationRequired(targetMachineNormalizationFlag);
        }

        if (configurationParams.containsKey("sourceIpGeoLocationFlag")) {
            boolean sourceIpGeoLocationFlag = configurationParams.get("sourceIpGeoLocationFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setSourceIpGeoLocationRequired(sourceIpGeoLocationFlag);
        }
        if (configurationParams.containsKey("targetIpGeoLocationFlag")) {
            boolean targetIpGeoLocationFlag = configurationParams.get("targetIpGeoLocationFlag").getParamFlag();
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setTargetIpGeoLocationRequired(targetIpGeoLocationFlag);
        }
    }

    @Override
    public void reset() throws Exception {
        // we suppose this will impact other configuration steps as well, therefor in this case of schema reset
        // we will actually reset all configuration definitions
        currGDSConfigurationState.reset();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.SCHEMA;
    }
}
