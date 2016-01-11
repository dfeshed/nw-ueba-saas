package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.RawModelScoreConfiguration;
import fortscale.services.configuration.gds.state.GDSRAWDataModelAndScoreState;
import fortscale.utils.ConversionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idanp on 1/11/2016.
 */
public class GDSRAWModelAndScoreConfigurator extends GDSBaseConfigurator  {

	private static final String LAST_STATE_PARAM = "lastState";
	private static final String TASK_NAME_PARAM = "taskName";
	private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";
	private static final String DATA_SOURCE_KEY ="rawPrevalanceConfigurationDataSourceKey";
	private static final String SCORE_FIELDS_CSV_PARAM = "scoreFieldsCSV";
	private static final String ADDITIONAL_SCORE_FIELDS_CSV_PARAM = "additionalScoreFieldsCSV";
	private static final String ADDITIONAL_FIELDS_CSV_PARAM = "additionalFieldsCSV";
	private static final String ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP = "additionalFiledToScoreFieldMapCSV";


	public GDSRAWModelAndScoreConfigurator() {
		configurationService = new RawModelScoreConfiguration();
	}

	GDSCLIInputHandler gdsInputHandler = new GDSCLIInputHandler();

	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
		Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

		ConfigurationParam lastState = gdsInputHandler.getParamConfiguration(paramsMap, LAST_STATE_PARAM);
		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

		ConfigurationParam scoreFeldsCSV  = gdsInputHandler.getParamConfiguration(paramsMap, SCORE_FIELDS_CSV_PARAM);
		ConfigurationParam additionalScoreFeldsCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_SCORE_FIELDS_CSV_PARAM);
		ConfigurationParam additionalFieldsCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_FIELDS_CSV_PARAM);
		ConfigurationParam additionalFiledToScoreFieldMapCSV = gdsInputHandler.getParamConfiguration(paramsMap, ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP);



		Map<String,String> scoresFieldMap = new LinkedHashMap<String,String>();
		Map<String,String> additionalScoreFeldsMap = new LinkedHashMap<String,String>();
		Map<String,String> additionalFieldsMap = new LinkedHashMap<String,String>();
		Map<String,String> additionalFiledToScoreFieldMap = new LinkedHashMap<String,String>();


		//Fields map (basic and additional)
		ConversionUtils.splitCSVtoMap(scoreFeldsCSV.getParamValue(), scoresFieldMap, ",");
		ConversionUtils.splitCSVtoMap(additionalScoreFeldsCSV.getParamValue(), additionalScoreFeldsMap, ",");
		ConversionUtils.splitCSVtoMap(additionalFieldsCSV.getParamValue(), additionalFieldsMap, ",");
		ConversionUtils.splitCSVtoMap(additionalFiledToScoreFieldMapCSV.getParamValue(), additionalFiledToScoreFieldMap, ",");

		Boolean sourceMachienFlag = gdsInputHandler.getParamConfiguration(paramsMap,"sourceMachienFlag").getParamFlag();
		Boolean destMachienFlag = gdsInputHandler.getParamConfiguration(paramsMap,"destMachienFlag").getParamFlag();
		Boolean countryToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"countryToScoreFlag").getParamFlag();
		Boolean actionTypeToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"actionTypeToScoreFlag").getParamFlag();
		Boolean dateTimeToScoreFlag = gdsInputHandler.getParamConfiguration(paramsMap,"dateTimeToScoreFlag").getParamFlag();


		GDSRAWDataModelAndScoreState gdsrawDataModelAndScoreState = currGDSConfigurationState.getRawDataModelAndScoreState();


		//populate the state
		gdsrawDataModelAndScoreState.setLastState(lastState.getParamValue());
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
		gdsrawDataModelAndScoreState.setAdditionalScoreFeldsMap(additionalScoreFeldsMap);
		gdsrawDataModelAndScoreState.setAdditionalFiledToScoreFieldMap(additionalFiledToScoreFieldMap);





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
