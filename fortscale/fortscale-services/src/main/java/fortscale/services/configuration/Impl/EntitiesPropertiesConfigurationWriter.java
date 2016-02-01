package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationWriterService;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesAdditionalField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesDeclaredField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesTable;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * writes the entities.properties from the stats to entities-overriding.properties, in web app and streaming
 * Created by galiar on 17/01/2016.
 */
public class EntitiesPropertiesConfigurationWriter extends ConfigurationWriterService {


	private static final String ENTITIES_OVERRIDING_RELATIVE_PATH = "/fortscale/fortscale-core/fortscale/fortscale-webapp/src/main/resources/META-INF/entities-overriding.properties";
	private static final String ENTITIES_OVERRIDING_STREAMING_RELATIVE_PATH = "/fortscale/streaming/config/entities-overriding-streaming.properties";

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


	private String streamingOverridingFilePath;
	protected File streamingOverridingFile;
	protected FileWriter streamingOverridingFileWriter;


	public EntitiesPropertiesConfigurationWriter()
	{
		logger = LoggerFactory.getLogger(EntitiesPropertiesConfigurationWriter.class);
		this.fileToConfigurePath = USER_HOME_DIR + ENTITIES_OVERRIDING_RELATIVE_PATH;
		this.streamingOverridingFilePath = USER_HOME_DIR + ENTITIES_OVERRIDING_STREAMING_RELATIVE_PATH;
	}


	@Override
	public boolean applyConfiguration() throws Exception {

		String line ="";

		String dataSourceName = gdsConfigurationState.getDataSourceName();

		String dataSourceList  = gdsConfigurationState.getExistingDataSources();
		//write the data source list
		line = String.format("leaf_entities=%s,%s",dataSourceList, dataSourceName);
		writeLineToFile(line, fileWriterToConfigure, true);
		writeLineToFile(line, streamingOverridingFileWriter, true);

		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		//write the table configs
		applyTableConfigs(dataSourceName);

		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		applyDeclaredFields(dataSourceName);

		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		applyHiddenFields(dataSourceName);

		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		applyAdditionalFields(dataSourceName);

		return true;
	}


	//write the fields declaration (even if it's a hidden field)
	private void applyDeclaredFields(String dataSourceName) throws Exception{

		writeLineToFile("####### Actual schema definition (logical to phisical mapping)", fileWriterToConfigure, true);
		writeLineToFile("####### Actual schema definition (logical to phisical mapping)", streamingOverridingFileWriter, true);
		List<GDSEntitiesPropertiesDeclaredField> declaredFields = gdsConfigurationState.getEntitiesPropertiesState().getDeclaredFields();
		for(GDSEntitiesPropertiesDeclaredField declaredField: declaredFields) {
			writeLineToFile(String.format("entities.%s.field.%s.column=%s", dataSourceName, declaredField.getFieldName(), declaredField.getFieldName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.column=%s", dataSourceName, declaredField.getFieldName(), declaredField.getFieldName()), streamingOverridingFileWriter, true);
		}
	}

	private void applyHiddenFields(String dataSourceName) throws Exception{
		//set the hidden fields
		writeLineToFile("######### Hidden Fields ###########", fileWriterToConfigure, true);
		writeLineToFile("######### Hidden Fields ###########", streamingOverridingFileWriter, true);
		List<GDSEntitiesPropertiesDeclaredField> hiddenFields = gdsConfigurationState.getEntitiesPropertiesState().getDeclaredFields().stream().filter(field -> !field.isInUse()).collect(Collectors.toList());
		for(GDSEntitiesPropertiesDeclaredField hiddenField:hiddenFields) {
			writeLineToFile(String.format("entities.%s.field.%s.attributes=internal", dataSourceName, hiddenField.getFieldName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.attributes=internal", dataSourceName, hiddenField.getFieldName()), streamingOverridingFileWriter, true);

			writeLineToFile(String.format("entities.%s.field.%s.enableByDefault=false", dataSourceName, hiddenField.getFieldName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.enableByDefault=false", dataSourceName, hiddenField.getFieldName()), streamingOverridingFileWriter, true);

		}
	}

	private void applyAdditionalFields(String dataSourceName) throws Exception{
		//write the additional fields configs
		writeLineToFile("####### Additional Fields ###########", fileWriterToConfigure, true);
		writeLineToFile("####### Additional Fields ###########", streamingOverridingFileWriter, true);


		for(GDSEntitiesPropertiesAdditionalField additionalField: gdsConfigurationState.getEntitiesPropertiesState().getAdditionalFields()){
			//field name
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), FIELD_NAME, additionalField.getFieldName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), FIELD_NAME, additionalField.getFieldName()), streamingOverridingFileWriter, true);

			//field type
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), TYPE, additionalField.getFieldType()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), TYPE, additionalField.getFieldType()), streamingOverridingFileWriter, true);
			switch (additionalField.getFieldType().toUpperCase()){
			case "STRING" :{
				writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), SEARCHABLE, additionalField.getSearchable()), fileWriterToConfigure, true);
				writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), SEARCHABLE, additionalField.getSearchable()), streamingOverridingFileWriter, true);
				break;
			}
			case "SELECT":{
				writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), VALUE_LIST, additionalField.getValueList()), fileWriterToConfigure, true);
				writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), VALUE_LIST, additionalField.getValueList()), streamingOverridingFileWriter, true);
				break;
			}
				default:break;
			}

			//field correlated score
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), CORRELATED_SCORE_FIELD, additionalField.getScoreField()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), CORRELATED_SCORE_FIELD, additionalField.getScoreField()), streamingOverridingFileWriter, true);

			//field rank
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), RANK, additionalField.getRank()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), RANK, additionalField.getRank()), streamingOverridingFileWriter, true);

			//field lov
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), LOV, additionalField.getLov()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), LOV, additionalField.getLov()), streamingOverridingFileWriter, true);

			//field enable by defalut
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), ENABLE_BY_DEFAULT, additionalField.getEnableByDefault()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, additionalField.getFieldId(), ENABLE_BY_DEFAULT, additionalField.getEnableByDefault()), streamingOverridingFileWriter, true);

			writeLineToFile("", fileWriterToConfigure, true);
			writeLineToFile("", streamingOverridingFileWriter, true);
		}
	}


	private void applyTableConfigs(String dataSourceName)throws Exception{
		GDSEntitiesPropertiesTable tableConfigs =  gdsConfigurationState.getEntitiesPropertiesState().getTableConfigs();
		if(tableConfigs == null){
			throw new  Exception(String.format(" Can't apply entities properties. No table configs for %s! ", dataSourceName));
		}

		writeLineToFile(String.format("######################################### %s ###########################################",dataSourceName), fileWriterToConfigure, true);
		writeLineToFile(String.format("######################################### %s ###########################################",dataSourceName), streamingOverridingFileWriter, true);

		//write id
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,ENTITY_ID,tableConfigs.getId()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,ENTITY_ID,tableConfigs.getId()),streamingOverridingFileWriter,true);

		//write name
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,ENTITY_NAME,tableConfigs.getName()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,ENTITY_NAME,tableConfigs.getName()),streamingOverridingFileWriter,true);

		//write nameForMenu
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,NAME_FOR_MENU,tableConfigs.getNameForMenu()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,NAME_FOR_MENU,tableConfigs.getNameForMenu()),streamingOverridingFileWriter,true);

		//write shortName
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SHORT_NAME,tableConfigs.getShortName()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SHORT_NAME,tableConfigs.getShortName()),streamingOverridingFileWriter,true);

		//write isAbstract
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,IS_ABSTRACT,tableConfigs.getIsAbstractStr()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,IS_ABSTRACT,tableConfigs.getIsAbstractStr()),streamingOverridingFileWriter,true);

		//write show in explore
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SHOW_IN_EXPLORE,tableConfigs.getShowInExploreStr()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SHOW_IN_EXPLORE,tableConfigs.getShowInExploreStr()),streamingOverridingFileWriter,true);

		//write db
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,DB,tableConfigs.getDb()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,DB,tableConfigs.getDb()),streamingOverridingFileWriter,true);

		//write score table
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SCORE_TABLE,tableConfigs.getTable()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,SCORE_TABLE,tableConfigs.getTable()),streamingOverridingFileWriter,true);

		//write performance table
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PERFORMANCE_TABLE,tableConfigs.getPerformanceTable()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PERFORMANCE_TABLE,tableConfigs.getPerformanceTable()),streamingOverridingFileWriter,true);

		//write partition
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PARTITION,tableConfigs.getPartition()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PARTITION,tableConfigs.getPartition()),streamingOverridingFileWriter,true);

		//write partition base field
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PARTITION_BASE_FIELD,tableConfigs.getPartitionBaseField()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,PARTITION_BASE_FIELD,tableConfigs.getPartitionBaseField()),streamingOverridingFileWriter,true);

		//write extends
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,EXTENDS,tableConfigs.getExtendsEntity()),fileWriterToConfigure,true);
		writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,EXTENDS,tableConfigs.getExtendsEntity()),streamingOverridingFileWriter,true);
	}

	@Override
	public boolean init() {
		Boolean result;
		try {
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			this.streamingOverridingFile = new File(this.streamingOverridingFilePath);
			this.streamingOverridingFileWriter = new FileWriter(this.streamingOverridingFile, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during EntititiesPropertiesConfigurationWriter init part execution - {} ", e.getMessage());
			System.out.println("There was an exception during execution please see more info at the log ");
			result = false;
		}

		return result;
	}

	@Override
	public Set<String> getAffectedConfigList() {
		return affectedConfigList;
	}
}
