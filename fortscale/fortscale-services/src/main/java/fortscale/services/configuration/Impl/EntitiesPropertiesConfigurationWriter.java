package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEntitiesPropertiesState;
import org.python.antlr.ast.Str;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;

/**
 * writes the entities.properties from the stats to entities-overriding.properties, in web app and streaming
 * Created by galiar on 17/01/2016.
 */
public class EntitiesPropertiesConfigurationWriter extends ConfigurationWriterService {


	private static final String ENTITIES_OVERRIDING_RELATIVE_PATH = "/fortscale/fortscale-core/fortscale/fortscale-webapp/src/main/resources/META-INF/entities-overriding.properties";
	private static final String ENTITIES_OVERRIDING_STREAMING_RELATIVE_PATH = "/fortscale/streaming/config/entities-overriding-streaming.properties";


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
		//get the table setting from the state
		GDSEntitiesPropertiesState entitiesPropertiesState = gdsConfigurationState.getEntitiesPropertiesState();
		Map<String,ConfigurationParam> tableConfigs = entitiesPropertiesState.getConfigurationParams().get(entitiesPropertiesState.TABLE_CONFIG_KEY);
		if(tableConfigs == null){
			throw new  Exception(String.format(" Can't apply entities properties. No table configs for %s! ", dataSourceName));
		}

		writeLineToFile(String.format("######################################### %s ###########################################",dataSourceName), fileWriterToConfigure, true);
		writeLineToFile(String.format("######################################### %s ###########################################",dataSourceName), streamingOverridingFileWriter, true);

		for (ConfigurationParam tableConfig: tableConfigs.values()){
			//write each line twice: once to the web-app entities overriding, once to streamin overriding
			writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,tableConfig.getParamName(),tableConfig.getParamValue()),fileWriterToConfigure,true);
			writeLineToFile(String.format("entities.%s.%s=%s",dataSourceName,tableConfig.getParamName(),tableConfig.getParamValue()),streamingOverridingFileWriter,true);
		}

		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		//write the fields declaration (even if it's a hidden field)
		writeLineToFile("####### Actual schema definition (logical to phisical mapping)", fileWriterToConfigure, true);
		writeLineToFile("####### Actual schema definition (logical to phisical mapping)", streamingOverridingFileWriter, true);
		Map<String,ConfigurationParam> declaredFields = entitiesPropertiesState.getConfigurationParams().get(entitiesPropertiesState.DECLARED_FIELDS_KEY);
		for(ConfigurationParam declaredField: declaredFields.values()) {
			writeLineToFile(String.format("entities.%s.field.%s.column=%s", dataSourceName, declaredField.getParamName(), declaredField.getParamValue()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.column=%s", dataSourceName, declaredField.getParamName(), declaredField.getParamValue()), streamingOverridingFileWriter, true);

		}
		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		//set the hidden fields
		writeLineToFile("Hidden Fields", fileWriterToConfigure, true);
		writeLineToFile("Hidden Fields", streamingOverridingFileWriter, true);
		Map<String,ConfigurationParam> hiddenFields = entitiesPropertiesState.getConfigurationParams().get(entitiesPropertiesState.HIDDEN_FIELDS);
		for(ConfigurationParam hiddenField: hiddenFields.values()) {
			writeLineToFile(String.format("entities.%s.field.%s.attributes=internal", dataSourceName, hiddenField.getParamName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.attributes=internal", dataSourceName, hiddenField.getParamName()), streamingOverridingFileWriter, true);

			writeLineToFile(String.format("entities.%s.field.%s.enableByDefault=false", dataSourceName, hiddenField.getParamName()), fileWriterToConfigure, true);
			writeLineToFile(String.format("entities.%s.field.%s.enableByDefault=false", dataSourceName, hiddenField.getParamName()), streamingOverridingFileWriter, true);

		}
		writeLineToFile("", fileWriterToConfigure, true);
		writeLineToFile("", streamingOverridingFileWriter, true);

		//write the additional fields configs
		writeLineToFile("Additional Fields", fileWriterToConfigure, true);
		writeLineToFile("Additional Fields", streamingOverridingFileWriter, true);
		for (String key: entitiesPropertiesState.getConfigurationParams().keySet()){
			if(key.startsWith(entitiesPropertiesState.FIELD_PREFIX)){
				String fieldName = key.split(entitiesPropertiesState.FIELD_PREFIX)[1];
				Map<String,ConfigurationParam> fieldConfigs = entitiesPropertiesState.getConfigurationParams().get(key);
				for(ConfigurationParam fieldConfig : fieldConfigs.values()){
					writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, fieldName, fieldConfig.getParamName(), fieldConfig.getParamValue()), fileWriterToConfigure, true);
					writeLineToFile(String.format("entities.%s.field.%s.%s=%s", dataSourceName, fieldName, fieldConfig.getParamName(), fieldConfig.getParamValue()), streamingOverridingFileWriter, true);
				}
				writeLineToFile("", fileWriterToConfigure, true);
				writeLineToFile("", streamingOverridingFileWriter, true);
			}
		}
		return true;
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
