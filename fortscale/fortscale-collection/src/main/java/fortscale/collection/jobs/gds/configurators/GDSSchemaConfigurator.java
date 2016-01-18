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

        String dataFields = paramsMap.get("dataFields").getParamValue();
        schemaDefinitionState.setDataFields(dataFields);

        String enrichFields = paramsMap.get("enrichFields").getParamValue();
        schemaDefinitionState.setEnrichFields(enrichFields);

        String enrichDelimiter = paramsMap.get("enrichDelimiter").getParamValue();
        schemaDefinitionState.setEnrichDelimiter(enrichDelimiter);

        String enrichTableName = paramsMap.get("enrichTableName").getParamValue();
        schemaDefinitionState.setEnrichTableName(enrichTableName);

        String scoreFields = paramsMap.get("scoreFields").getParamValue();
        schemaDefinitionState.setScoreFields(scoreFields);

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

        String scoreFieldsCSV = paramsMap.get("scoreFieldsCSV").getParamValue();
        schemaDefinitionState.setScoreFieldsCSV(scoreFieldsCSV);

        String scoreFieldToFieldNameCSV = paramsMap.get("scoreFieldToFieldNameCSV").getParamValue();

        String additionalFieldsCSV = paramsMap.get("additionalFieldsCSV").getParamValue();
        schemaDefinitionState.setAdditionalFieldsCSV(additionalFieldsCSV);

        String additionalScoreFieldsCSV = paramsMap.get("additionalScoreFieldsCSV").getParamValue();
        schemaDefinitionState.setAdditionalScoreFieldsCSV(additionalScoreFieldsCSV);

        String additionalPopulatedScoreFieldsCSV = paramsMap.get("additionalPopulatedScoreFieldsCSV").getParamValue();
        schemaDefinitionState.setAdditionalPopulatedScoreFieldsCSV(additionalPopulatedScoreFieldsCSV);

        String additionalFiledToScoreFieldMapCSV = paramsMap.get("additionalFiledToScoreFieldMapCSV").getParamValue();
        schemaDefinitionState.setAdditionalFiledToScoreFieldMapCSV(additionalFiledToScoreFieldMapCSV);

        String populatedScoreFieldsCSV = paramsMap.get("populatedScoreFieldsCSV").getParamValue();

        configureFields(dataFields, scoreFieldsCSV + "," + additionalScoreFieldsCSV, populatedScoreFieldsCSV + additionalPopulatedScoreFieldsCSV, scoreFieldToFieldNameCSV + "," + additionalFiledToScoreFieldMapCSV);
    }

    private void configureFields(String fieldsCSV, String scoreFieldsCSV, String populatedScoreFieldsCSV, String scoreFieldsToFieldCSV) {
        if (fieldsCSV == null || "".equals(fieldsCSV)) {
            return;
        }

        FieldMetadataDictionary fieldMetadataDictionary = currGDSConfigurationState.getSchemaDefinitionState().getFieldMetadataDictionary();

        Map<String, String> fieldNameToTypeMap = ConversionUtils.convertCSVToMap(fieldsCSV);

        for (Map.Entry<String, String> fieldNameToType : fieldNameToTypeMap.entrySet()) {
            String fieldName = fieldNameToType.getKey();
            String type = fieldNameToType.getValue();

            FieldMetadata fieldMetadata = new FieldMetadata(fieldName, FieldType.valueOf(type.toUpperCase()));

            fieldMetadataDictionary.addField(fieldMetadata);
        }

        Map<String, String> scoreFieldNameToTypeMap = ConversionUtils.convertCSVToMap(scoreFieldsCSV);
        Map<String, String> scoreFieldNameToFieldMap = ConversionUtils.convertCSVToMap(scoreFieldsToFieldCSV);

        Set<String> populatedScoreFieldsSet = ConversionUtils.convertCSVToSet(populatedScoreFieldsCSV);

        for (Map.Entry<String, String> scoreFieldToType : scoreFieldNameToTypeMap.entrySet()) {
            String scoreFieldName = scoreFieldToType.getKey();

            boolean isInUse = populatedScoreFieldsSet.contains(scoreFieldName);
            ScoreFieldMetadata scoreFieldMetadata = new ScoreFieldMetadata(scoreFieldName, isInUse);

            fieldMetadataDictionary.addScoreField(scoreFieldMetadata);

            if (scoreFieldNameToFieldMap.containsKey(scoreFieldName)) {
                String fieldName = scoreFieldNameToFieldMap.get(scoreFieldName);
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
