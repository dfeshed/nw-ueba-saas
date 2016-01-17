package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.RawModelScoreConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSRAWDataModelAndScoreState;

import java.util.Map;

/**
 * Created by idanp on 1/11/2016.
 */
public class GDSRawModelAndScoreConfigurator extends GDSBaseConfigurator  {

	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";
	private static final String DATA_SOURCE_KEY ="rawPrevalanceConfigurationDataSourceKey";
	private static final String SCORE_FIELDS_CSV_PARAM = "scoreFieldsCSV";
	private static final String ADDITIONAL_SCORE_FIELDS_CSV_PARAM = "additionalScoreFieldsCSV";
	private static final String ADDITIONAL_FIELDS_CSV_PARAM = "additionalFieldsCSV";
	private static final String ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP = "additionalFiledToScoreFieldMapCSV";


	public GDSRawModelAndScoreConfigurator() {
		configurationWriterService = new RawModelScoreConfigurationWriter();
	}



	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
		Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

		String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

		ConfigurationParam scoreFeldsCSV  = gdsInputHandler.getParamConfiguration(paramsMap, SCORE_FIELDS_CSV_PARAM);
		ConfigurationParam additionalScoreFieldsCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_SCORE_FIELDS_CSV_PARAM);
		ConfigurationParam additionalFieldsCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_FIELDS_CSV_PARAM);
		ConfigurationParam additionalFiledToScoreFieldMapCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP);


		//Fields map (basic and additional)
		Map<String,String> scoresFieldMap = gdsInputHandler.splitCSVtoMap(scoreFeldsCSV.getParamValue());
		Map<String,String> additionalScoreFieldsMap = gdsInputHandler.splitCSVtoMap(additionalScoreFieldsCSV.getParamValue());
		Map<String,String> additionalFieldsMap = gdsInputHandler.splitCSVtoMap(additionalFieldsCSV.getParamValue());
		Map<String,String> additionalFiledToScoreFieldMap = gdsInputHandler.splitCSVtoMap(additionalFiledToScoreFieldMapCSV.getParamValue());

		Boolean sourceMachienFlag = gdsInputHandler.getParamConfiguration(paramsMap,"sourceMachineFlag").getParamFlag();
		Boolean destMachienFlag = gdsInputHandler.getParamConfiguration(paramsMap,"destMachineFlag").getParamFlag();
		Boolean countryToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"countryToScoreFlag").getParamFlag();
		Boolean actionTypeToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"actionTypeToScoreFlag").getParamFlag();
		Boolean dateTimeToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"dateTimeToScoreFlag").getParamFlag();


		GDSRAWDataModelAndScoreState gdsrawDataModelAndScoreState = currGDSConfigurationState.getRawDataModelAndScoreState();


		//populate the state
		gdsrawDataModelAndScoreState.setLastState(lastState);
		gdsrawDataModelAndScoreState.setTaskName(taskName.getParamValue());
		gdsrawDataModelAndScoreState.setOutputTopic(outputTopic.getParamValue());
		gdsrawDataModelAndScoreState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);

		gdsrawDataModelAndScoreState.setSourceMachienFlag(sourceMachienFlag);
		gdsrawDataModelAndScoreState.setDestMachienFlag(destMachienFlag);
		gdsrawDataModelAndScoreState.setCountryToScoreFlag(countryToScoreFlag);
		gdsrawDataModelAndScoreState.setActionTypeToScoreFlag(actionTypeToScoreFlag);
		gdsrawDataModelAndScoreState.setDateTimeToScoreFlag(dateTimeToScoreFlag);

		gdsrawDataModelAndScoreState.setDataSourcesConfigurationKey(DATA_SOURCE_KEY);

		gdsrawDataModelAndScoreState.setScoresFieldMap(scoresFieldMap);
		gdsrawDataModelAndScoreState.setAdditionalFieldsMap(additionalFieldsMap);
		gdsrawDataModelAndScoreState.setAdditionalScoreFeldsMap(additionalScoreFieldsMap);
		gdsrawDataModelAndScoreState.setAdditionalFiledToScoreFieldMap(additionalFiledToScoreFieldMap);

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
