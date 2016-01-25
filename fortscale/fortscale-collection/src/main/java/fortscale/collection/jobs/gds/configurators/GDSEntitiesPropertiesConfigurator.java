package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.EntitiesPropertiesConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEntitiesPropertiesState;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesAdditionalField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesDeclaredField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesTable;

import java.util.Map;

/**
 * sets the raw data from the populator into a structured form in the  state.
 *
 * Created by galiar on 17/01/2016.
 */
public class GDSEntitiesPropertiesConfigurator extends GDSBaseConfigurator {

	private static final String TABLE_CONFIG_KEY = "tableConfigs";
	private static final String DECLARED_FIELDS_KEY = "declaredFields";
	private static final String HIDDEN_FIELDS = "hiddenFields";
	private static final String FIELD_PREFIX = "Field_";

	//table constants
	private static final String ENTITY_ID = "id";
	private static final String ENTITY_NAME = "name";
	private static final String SHORT_NAME = "shortName";
	private static final String NAME_FOR_MENU = "nameForMenu";
	private static final String IS_ABSTRACT = "is_abstract";
	private static final String SHOW_IN_EXPLORE = "show_in_explore";
	private static final String DB = "db";
	private static final String SCORE_TABLE = "table";
	private static final String PERFORMANCE_TABLE = "performance_table";
	private static final String PARTITION = "partition";
	private static final String PARTITION_BASE_FIELD = "partitionBaseField";
	private static final String EXTENDS = "extends";

	//field constants
	private static final String FIELD_ID = "id";
	private static final String FIELD_NAME = "name";
	private static final String TYPE = "type";
	private static final String VALUE_LIST = "valueList";
	private static final String SEARCHABLE = "searchable";
	private static final String RANK = "rank";
	private static final String CORRELATED_SCORE_FIELD = "score";
	private static final String LOV = "lov";
	private static final String ENABLE_BY_DEFAULT = "enableByDefault";



	public GDSEntitiesPropertiesConfigurator() {
		configurationWriterService = new EntitiesPropertiesConfigurationWriter();
	}

	@Override
	public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

		configureEntityTable(configurationParams);

		configureDeclaredFields(configurationParams);

		configureAdditionalFields(configurationParams);

	}

	private void configureEntityTable(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

		Map<String, ConfigurationParam> tableConfigMap = configurationParams.get(TABLE_CONFIG_KEY);
		if(tableConfigMap == null){
			throw new Exception("Can't configure entities.properties: no table config found.");
		}
		GDSEntitiesPropertiesState entitiesState = currGDSConfigurationState.getEntitiesPropertiesState();
		GDSEntitiesPropertiesTable tableConfig = new GDSEntitiesPropertiesTable();
		tableConfig.setId(tableConfigMap.get(ENTITY_ID).getParamValue());
		tableConfig.setName(tableConfigMap.get(ENTITY_NAME).getParamValue());
		tableConfig.setNameForMenu(tableConfigMap.get(NAME_FOR_MENU).getParamValue());
		tableConfig.setShortName(tableConfigMap.get(SHORT_NAME).getParamValue());
		tableConfig.setIsAbstractStr(tableConfigMap.get(IS_ABSTRACT).getParamValue());
		tableConfig.setShowInExploreStr(tableConfigMap.get(SHOW_IN_EXPLORE).getParamValue());
		tableConfig.setExtendsEntity(tableConfigMap.get(EXTENDS).getParamValue());
		tableConfig.setDb(tableConfigMap.get(DB).getParamValue());
		tableConfig.setTable(tableConfigMap.get(SCORE_TABLE).getParamValue());
		tableConfig.setPerformanceTable(tableConfigMap.get(PERFORMANCE_TABLE).getParamValue());
		tableConfig.setPartition(tableConfigMap.get(PARTITION).getParamValue());
		tableConfig.setPartitionBaseField(tableConfigMap.get(PARTITION_BASE_FIELD).getParamValue());

		entitiesState.setTableConfigs(tableConfig);
	}



	private void configureDeclaredFields(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception{

		Map<String, ConfigurationParam> declaredFields = configurationParams.get(DECLARED_FIELDS_KEY);
		Map<String, ConfigurationParam> hiddenFields = configurationParams.get(HIDDEN_FIELDS);
		if(declaredFields == null || hiddenFields == null ){
			throw new Exception("Can't configure entities.properties: no declared or hidden fields found.");
		}
		boolean isInUse;
		for (String fieldName: declaredFields.keySet()){
			isInUse = !hiddenFields.containsKey(fieldName);
			currGDSConfigurationState.getEntitiesPropertiesState().addDeclaredField(new GDSEntitiesPropertiesDeclaredField(fieldName,isInUse));
		}

	}

	private void configureAdditionalFields(Map<String, Map<String, ConfigurationParam>> configurationParams) {
		for (String key: configurationParams.keySet()) {
			if (key.startsWith(FIELD_PREFIX)) {
				configureAdditionalField(configurationParams.get(key));
			}
		}
	}

	private void configureAdditionalField(Map<String,ConfigurationParam> additionalFieldMap){
		GDSEntitiesPropertiesAdditionalField additionalField = new GDSEntitiesPropertiesAdditionalField();
		additionalField.setFieldId(additionalFieldMap.get(FIELD_ID).getParamValue());
		additionalField.setFieldName(additionalFieldMap.get(FIELD_NAME).getParamValue());
		additionalField.setFieldType(additionalFieldMap.get(TYPE).getParamValue());
		additionalField.setRank(additionalFieldMap.get(RANK).getParamValue());
		additionalField.setLov(additionalFieldMap.get(LOV).getParamValue());
		additionalField.setEnableByDefault(additionalFieldMap.get(ENABLE_BY_DEFAULT).getParamValue());

		if(additionalFieldMap.get(CORRELATED_SCORE_FIELD) != null) {
			additionalField.setScoreField(additionalFieldMap.get(CORRELATED_SCORE_FIELD).getParamValue());
		}
		if(additionalFieldMap.get(SEARCHABLE) != null){
			additionalField.setSearchable(additionalFieldMap.get(SEARCHABLE).getParamValue());
		}
		if(additionalFieldMap.get(VALUE_LIST) != null){
			additionalField.setValueList(additionalFieldMap.get(VALUE_LIST).getParamValue());
		}
		currGDSConfigurationState.getEntitiesPropertiesState().addAdditionalField(additionalField);
	}

	@Override
	public void reset() throws Exception {
		currGDSConfigurationState.getEntitiesPropertiesState().reset();
	}

	@Override
	public GDSConfigurationType getType() {
		return GDSConfigurationType.ENTITIES_PROPERTIES;
	}
}
