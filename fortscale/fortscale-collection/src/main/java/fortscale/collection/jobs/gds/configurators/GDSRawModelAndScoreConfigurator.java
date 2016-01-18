package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.RawModelScoreConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSRAWDataModelAndScoreState;
import fortscale.services.configuration.gds.state.field.FieldMetadataDictionary;
import fortscale.services.configuration.gds.state.field.FieldMetadataExtractor;
import fortscale.services.configuration.gds.state.field.ScoreFieldMetadata;
import fortscale.utils.ConversionUtils;

import java.util.Map;

/**
 * Created by idanp on 1/11/2016.
 */
public class GDSRawModelAndScoreConfigurator extends GDSBaseConfigurator  {

	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";
	private static final String DATA_SOURCE_KEY ="rawPrevalanceConfigurationDataSourceKey";

	public GDSRawModelAndScoreConfigurator() {
		configurationWriterService = new RawModelScoreConfigurationWriter();
	}

	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
		Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

		String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

		FieldMetadataDictionary fieldMetadataDictionary = currGDSConfigurationState.getSchemaDefinitionState().getFieldMetadataDictionary();

		String baseScoreFieldsCSV = FieldMetadataExtractor.extractBaseScoreFieldsCSV(fieldMetadataDictionary);
		String additionalScoreFieldsCSV = FieldMetadataExtractor.extractAdditionalScoreFieldsCSV(fieldMetadataDictionary);
		String additionalFieldsCSV = FieldMetadataExtractor.extractAdditionalFieldsCSV(fieldMetadataDictionary);
		String additionalScoreFieldToFieldNameCSV = FieldMetadataExtractor.extractAdditionalScoreFieldToFieldNameCSV(fieldMetadataDictionary);

		//Fields map (basic and additional)
		Map<String,String> baseScoreFieldMap = ConversionUtils.splitCSVtoMap(baseScoreFieldsCSV);
		Map<String,String> additionalScoreFieldsMap = ConversionUtils.splitCSVtoMap(additionalScoreFieldsCSV);
		Map<String,String> additionalFieldsMap = ConversionUtils.splitCSVtoMap(additionalFieldsCSV);
		Map<String,String> additionalFieldToScoreFieldMap = ConversionUtils.splitCSVtoMap(additionalScoreFieldToFieldNameCSV);

		ScoreFieldMetadata sourceMachineScoreField = fieldMetadataDictionary.getScoreFieldMetadataByName("source_machine_score");
		boolean sourceMachineScoreInUse = sourceMachineScoreField != null && sourceMachineScoreField.isInUse();

		ScoreFieldMetadata destMachineScoreField = fieldMetadataDictionary.getScoreFieldMetadataByName("destination_machine_score");
		boolean destMachineScoreInUse = destMachineScoreField != null && destMachineScoreField.isInUse();

		ScoreFieldMetadata countryScore = fieldMetadataDictionary.getScoreFieldMetadataByName("country_score");
		boolean countryScoreInUse = countryScore != null && countryScore.isInUse();

		ScoreFieldMetadata actionTypeScore = fieldMetadataDictionary.getScoreFieldMetadataByName("action_type_score");
		boolean actionTypeScoreInUse = actionTypeScore != null && actionTypeScore.isInUse();

		ScoreFieldMetadata dateTimeScore = fieldMetadataDictionary.getScoreFieldMetadataByName("date_time_score");
		boolean dateTimeScoreInUse = dateTimeScore != null && dateTimeScore.isInUse();


		GDSRAWDataModelAndScoreState gdsrawDataModelAndScoreState = currGDSConfigurationState.getRawDataModelAndScoreState();


		//populate the state
		gdsrawDataModelAndScoreState.setLastState(lastState);
		gdsrawDataModelAndScoreState.setTaskName(taskName.getParamValue());
		gdsrawDataModelAndScoreState.setOutputTopic(outputTopic.getParamValue());
		gdsrawDataModelAndScoreState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);

		gdsrawDataModelAndScoreState.setSourceMachienFlag(sourceMachineScoreInUse);
		gdsrawDataModelAndScoreState.setDestMachienFlag(destMachineScoreInUse);
		gdsrawDataModelAndScoreState.setCountryToScoreFlag(countryScoreInUse);
		gdsrawDataModelAndScoreState.setActionTypeToScoreFlag(actionTypeScoreInUse);
		gdsrawDataModelAndScoreState.setDateTimeToScoreFlag(dateTimeScoreInUse);

		gdsrawDataModelAndScoreState.setDataSourcesConfigurationKey(DATA_SOURCE_KEY);

		gdsrawDataModelAndScoreState.setScoresFieldMap(baseScoreFieldMap);
		gdsrawDataModelAndScoreState.setAdditionalFieldsMap(additionalFieldsMap);
		gdsrawDataModelAndScoreState.setAdditionalScoreFeldsMap(additionalScoreFieldsMap);
		gdsrawDataModelAndScoreState.setAdditionalFiledToScoreFieldMap(additionalFieldToScoreFieldMap);

		currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(taskName.getParamValue());
	}

	@Override
	public void reset() throws Exception {
		currGDSConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState().reset();
	}

	@Override
	public GDSConfigurationType getType() {
		return GDSConfigurationType.RAW_MODEL_AND_SCORE;
	}


}
