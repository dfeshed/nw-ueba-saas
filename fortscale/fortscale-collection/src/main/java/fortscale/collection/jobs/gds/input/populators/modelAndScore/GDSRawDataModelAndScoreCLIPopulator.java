package fortscale.collection.jobs.gds.input.populators.modelAndScore;

import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
import fortscale.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Model and scoring CLI populator
 *
 * Created by idanp on 1/10/2016.
 */
public class GDSRawDataModelAndScoreCLIPopulator implements GDSConfigurationPopulator {


	private static final String SCORE_FIELDS_CSV_PARAM = "scoreFieldsCSV";
	private static final String ADDITIONAL_SCORE_FIELDS_CSV_PARAM = "additionalScoreFieldsCSV";
	private static final String ADDITIONAL_FIELDS_CSV_PARAM = "additionalFieldsCSV";
	private static final String ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP = "additionalFiledToScoreFieldMapCSV";
	private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";
	private static final String DATA_SOURCE_KEY ="rawPrevalanceConfigurationDataSourceKey";
	private static final String TASK_NAME_PARAM = "taskName";
	private static final String LAST_STATE_PARAM = "lastState";
	private static final String OUTPUT_TOPIC_PARAM = "outPutTopic";


	@Override
	public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {


		Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();
		HashMap<String, ConfigurationParam> paramsMap = new HashMap<>();
		GDSCLIInputHandler gdsInputHandler = new GDSCLIInputHandler();

		configurationsMap.put(GDS_CONFIG_ENTRY, paramsMap);

		String scoreFieldsCSV = currentConfigurationState.getSchemaDefinitionState().getScoreFields();
		String additionalScoreFieldsCSV = currentConfigurationState.getSchemaDefinitionState().getAdditionalScoreFieldsCSV();
		String additionalFieldsCSV = currentConfigurationState.getSchemaDefinitionState().getAdditionalFieldsCSV();
		String additionalFiledToScoreFieldMapCSV = currentConfigurationState.getSchemaDefinitionState().getAdditionalFiledToScoreFieldMapCSV();

		paramsMap.put(SCORE_FIELDS_CSV_PARAM, new ConfigurationParam(SCORE_FIELDS_CSV_PARAM,false,scoreFieldsCSV));
		paramsMap.put(ADDITIONAL_SCORE_FIELDS_CSV_PARAM,new ConfigurationParam(ADDITIONAL_SCORE_FIELDS_CSV_PARAM,false,additionalScoreFieldsCSV));
		paramsMap.put(ADDITIONAL_FIELDS_CSV_PARAM, new ConfigurationParam(ADDITIONAL_FIELDS_CSV_PARAM, false, additionalFieldsCSV));
		paramsMap.put(ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP,new ConfigurationParam(ADDITIONAL_FIELD_TO_ADDITIONAL_SCORE_FIELD_MAP,false,additionalFiledToScoreFieldMapCSV));

		paramsMap.put(LAST_STATE_PARAM, new ConfigurationParam(LAST_STATE_PARAM,false,"HDFSWriterStreamTask"));
		paramsMap.put(TASK_NAME_PARAM,new ConfigurationParam(TASK_NAME_PARAM,false,"MultipleEventsPrevalenceModelStreamTask"));
		paramsMap.put(OUTPUT_TOPIC_PARAM, new ConfigurationParam(OUTPUT_TOPIC_PARAM, false, "fortscale-generic-data-access-event-score"));
		paramsMap.put(DATA_SOURCE_KEY,new ConfigurationParam(DATA_SOURCE_KEY,false,"fortscale.events-prevalence-stream-managers.data-sources"));


		Map<String,String> scoresFieldMap = ConversionUtils.convertFieldsCSVToMap(scoreFieldsCSV);

		//For each potential basic score field ask if we want to populate it
		for (Map.Entry<String,String> entry : scoresFieldMap.entrySet())
		{
			String scoreField = entry.getKey();

			System.out.println(String.format("Does %s field should be scored (y/n)?", scoreField));

			if (gdsInputHandler.getYesNoInput()) {
				paramsMap.put(scoreField, new ConfigurationParam(scoreField, true, scoreField));
			}
		}

		ConfigurationParam result;

		result = gdsInputHandler.getParamConfiguration(paramsMap,"source_machine_score");
		Boolean sourceMachineFlag = result != null ? result.getParamFlag() : false;

		result = gdsInputHandler.getParamConfiguration(paramsMap, "destination_machine_score");
		Boolean destMachineFlag = result != null ? result.getParamFlag() : false;

		result = gdsInputHandler.getParamConfiguration(paramsMap, "country_score");
		Boolean countryToScoreFlag = result != null ? result.getParamFlag() : false;

		result = gdsInputHandler.getParamConfiguration(paramsMap, "action_type_score");
		Boolean actionTypeToScoreFlag = result != null ? result.getParamFlag() : false;

		result = gdsInputHandler.getParamConfiguration(paramsMap, "date_time_score");
		Boolean dateTimeToScoreFlag = result != null ? result.getParamFlag() : false;

		paramsMap.put("sourceMachineFlag",new ConfigurationParam("sourceMachineFlag",sourceMachineFlag,""));
		paramsMap.put("destMachineFlag",new ConfigurationParam("destMachineFlag",destMachineFlag,""));
		paramsMap.put("countryToScoreFlag",new ConfigurationParam("countryToScoreFlag",countryToScoreFlag,""));
		paramsMap.put("actionTypeToScoreFlag",new ConfigurationParam("actionTypeToScoreFlag",actionTypeToScoreFlag,""));
		paramsMap.put("dateTimeToScoreFlag",new ConfigurationParam("dateTimeToScoreFlag",dateTimeToScoreFlag,""));

		return configurationsMap;
	}
}
