package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.collection.jobs.gds.input.GDSCLIInputHandler;
import fortscale.collection.jobs.gds.input.GDSInputHandler;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;
import fortscale.services.configuration.gds.state.field.FieldMetadata;
import fortscale.services.configuration.gds.state.field.FieldMetadataDictionary;
import fortscale.services.configuration.gds.state.field.FieldMetadataExtractor;

import java.util.*;

/**
 * Created by galiar on 17/01/2016.
 * creates entities.properties config's block.
 * according to the definition the user defined in the schema definition step, the populator create the adjusted entities
 * properties - declare them for the new data entity.
 * if the user has declared additional fields (not part of base entity), then she will configure them here properly, with
 * all the configurations that the UI need.
 */
public class GDSEntitiesPropertiesCLIPopulator implements GDSConfigurationPopulator {

	private GDSInputHandler gdsInputHandler = new GDSCLIInputHandler();
	private GDSCompositeConfigurationState currentConfigurationState;

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
	private static final String SELECT = "SELECT";
	private static final String VALUE_LIST = "valueList";
	private static final String SEARCHABLE = "searchable";
	private static final String RANK = "rank";
	private static final String CORRELATED_SCORE_FIELD = "score";
	private static final String LOV = "lov";
	private static final String ENABLE_BY_DEFAULT = "enableByDefault";

	@Override
	public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(
			GDSCompositeConfigurationState currentConfigurationState) throws Exception {

		this.currentConfigurationState = currentConfigurationState;
		Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();

		//configure the table general settings
		HashMap<String, ConfigurationParam> tableConfigsMap = configureTable();
		configurationsMap.put(currentConfigurationState.getEntitiesPropertiesState().TABLE_CONFIG_KEY,tableConfigsMap);

		//declare the fields
		FieldMetadataDictionary fields = currentConfigurationState.getFieldMetadataDictionary();
		Map<String, Map<String, ConfigurationParam>> hiddenAndDeclaredFields = declareFields(fields);
		for (String entityField: hiddenAndDeclaredFields.keySet()){
			configurationsMap.put(entityField, hiddenAndDeclaredFields.get(entityField));
		}

		//configure additional fields
		Map<String,FieldMetadata> additionalFields = FieldMetadataExtractor.extractAdditionalFields(fields);
		for(FieldMetadata additionalField: additionalFields.values()){
			configurationsMap.put( currentConfigurationState.getEntitiesPropertiesState().FIELD_PREFIX +additionalField.getFieldName(), configureNewField(additionalField));
		}

		return configurationsMap;
	}

	/**
	 * all the fields in entity should be declared, even the derived ones.
	 * @return
	 */
	private  Map<String, Map<String, ConfigurationParam>>  declareFields(FieldMetadataDictionary fields) {

		Map<String, Map<String, ConfigurationParam>> allfields = new HashMap<>();
		Map<String, ConfigurationParam> declaredFields = new HashMap<>();
		Map<String, ConfigurationParam> hiddenFields = new HashMap<>();

		allfields.put(currentConfigurationState.getEntitiesPropertiesState().DECLARED_FIELDS_KEY, declaredFields);
		allfields.put(currentConfigurationState.getEntitiesPropertiesState().HIDDEN_FIELDS,hiddenFields);

		for (FieldMetadata field: fields.getAllFields()){
			//declare it in entities.properties - all derived fields must be declared in the entity.
			String fieldName = field.getFieldName();
			declaredFields.put(fieldName,new ConfigurationParam(fieldName,false,fieldName));
			if(!field.isInUse()){
				//set it to hidden
				hiddenFields.put(fieldName,new ConfigurationParam(fieldName,false,fieldName));
			}
		}
		return allfields ;
	}

	/**
	 * configure the settings of the GDS entity table - the table's name, the table that it extends etc.
	 * @return
	 */
	private HashMap<String, ConfigurationParam> configureTable() throws Exception{
		//sets the entity table configs
		HashMap<String, ConfigurationParam> tableConfigslMap = new HashMap<>();

		//name -- ask the user
		System.out.println(String.format("Please enter the display name of the data source: %s", currentConfigurationState.getDataSourceName()) );
		String entityName = gdsInputHandler.getInput();
		tableConfigslMap.put(ENTITY_NAME,new ConfigurationParam(ENTITY_NAME,false,entityName));

		//short_name -- ask the user
		System.out.println(String.format("Please enter the short display name for the %s ", currentConfigurationState.getDataSourceName(), currentConfigurationState.getDataSourceName()));
		String shortName = gdsInputHandler.getInput();
		tableConfigslMap.put(SHORT_NAME,new ConfigurationParam(SHORT_NAME,false,shortName));

		//nameForMenu -- asl the user
		System.out.println(String.format("Please enter the display name in pop-up menus of %s. convention: '%s events'", currentConfigurationState.getDataSourceName(),shortName) );
		String nameForMenu = gdsInputHandler.getInput();
		tableConfigslMap.put(NAME_FOR_MENU,new ConfigurationParam(NAME_FOR_MENU,false,nameForMenu));


		//id
		tableConfigslMap.put(ENTITY_ID, new ConfigurationParam(ENTITY_ID,false,currentConfigurationState.getDataSourceName()));

		//is_abstract -- no
		tableConfigslMap.put(IS_ABSTRACT, new ConfigurationParam(IS_ABSTRACT, false, "false"));
		//show_in_explore -- yes
		tableConfigslMap.put(SHOW_IN_EXPLORE, new ConfigurationParam(SHOW_IN_EXPLORE, false, "true"));

		//extends
		String baseEntity = currentConfigurationState.getEntityType().getEntityName();
		tableConfigslMap.put(EXTENDS, new ConfigurationParam(EXTENDS, false, baseEntity));

		//db --sql
		tableConfigslMap.put(DB, new ConfigurationParam(DB, false, "MySQL"));

		//physical table
		tableConfigslMap.put(SCORE_TABLE, new ConfigurationParam(SCORE_TABLE, false, currentConfigurationState.getSchemaDefinitionState().getScoreTableName()));

		//performance_table  - top table
		tableConfigslMap.put(PERFORMANCE_TABLE, new ConfigurationParam(PERFORMANCE_TABLE, false, currentConfigurationState.getSchemaDefinitionState().getScoreTableName()+"_top"));

		//partition - daily
		tableConfigslMap.put(PARTITION, new ConfigurationParam(PARTITION, false, "daily"));

		//partition.base.field
		tableConfigslMap.put(PARTITION_BASE_FIELD, new ConfigurationParam(PARTITION_BASE_FIELD, false, "event_time_utc"));
		return tableConfigslMap;
	}

	/**
	 * configure a new field in entities properties.
	 * new field is a field that wasn't derived from any base entity.
	 *
	 */
	private HashMap<String, ConfigurationParam> configureNewField(FieldMetadata field) throws Exception{

		HashMap<String, ConfigurationParam> entityFieldMap = new HashMap<>();

		//id
		String id = field.getFieldName();
		entityFieldMap.put(FIELD_ID,new ConfigurationParam(FIELD_ID,false,id));

		//name
		System.out.println(String.format("Please enter the name you wish to show in app for field: %s ",field.getFieldName()));
		String name = gdsInputHandler.getInput();
		entityFieldMap.put(FIELD_NAME,new ConfigurationParam(FIELD_NAME,false,name));

		//type
		String[] allowedTypesArr = {"STRING","SELECT","NUMBER"," BOOLEAN","TIMESTAMP","DATE_TIME","DURATION"};
		Set<String> allowedTypes =  new HashSet<>(Arrays.asList(allowedTypesArr));
		System.out.println(String.format("Please enter the type of field: %s. options are: STRING, SELECT, NUMBER, BOOLEAN, TIMESTAMP, DATE_TIME, DURATION ",field.getFieldName()));
		String type = gdsInputHandler.getInput(allowedTypes);
		entityFieldMap.put(TYPE,new ConfigurationParam(TYPE,false,type));

		//in case of select - ask for value list
		if (type.toUpperCase().equals(SELECT)) {

			System.out.println(String.format("Please enter the values list for the select type: enter the values list in one string separated by commas (',')"));
			String valueList = gdsInputHandler.getInput();
			entityFieldMap.put(VALUE_LIST,new ConfigurationParam(VALUE_LIST,false,valueList));
		}

		else if(type.toUpperCase().equals("STRING")){
			//set searchable = true
			entityFieldMap.put(SEARCHABLE,new ConfigurationParam(SEARCHABLE,true,"true"));
		}
		//rank - always 10
		entityFieldMap.put(RANK,new ConfigurationParam(RANK,false,"10"));

		//score - find with the field to score map
		FieldMetadata correlatedScoreField = currentConfigurationState.getFieldMetadataDictionary().getScoreFieldMetaData(field);
		if ( correlatedScoreField!= null){
			entityFieldMap.put(CORRELATED_SCORE_FIELD,new ConfigurationParam(CORRELATED_SCORE_FIELD,false,correlatedScoreField.getFieldName()));
		}

		//lov - always true
		entityFieldMap.put(LOV,new ConfigurationParam(LOV,true,"true"));

		//enabledByDefault
		System.out.println(String.format("Does %s should appear in the default table of entity?", field.getFieldName()));
		Boolean enableByDefalut = gdsInputHandler.getYesNoInput();
		entityFieldMap.put(ENABLE_BY_DEFAULT,new ConfigurationParam(ENABLE_BY_DEFAULT,enableByDefalut,enableByDefalut.toString()));

		return entityFieldMap;
	}
}
