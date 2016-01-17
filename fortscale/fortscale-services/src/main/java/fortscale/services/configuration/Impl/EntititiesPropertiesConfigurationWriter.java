package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationWriterService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

/**
 * writes the entities.properties from the stats to entities-overriding.properties, in web app and streaming
 * Created by galiar on 17/01/2016.
 */
public class EntititiesPropertiesConfigurationWriter extends ConfigurationWriterService {


	private static final String ENTITIES_OVERRIDING_RELATIVE_PATH = "/fortscale/fortscale-core/fortscale/fortscale-webapp/src/main/resources/META-INF/entities-overriding.properties";
	private static final String ENTITIES_OVERRIDING_STREAMING_RELATIVE_PATH = "fortscale/fortscale-core/fortscale/fortscale-streaming/config/entities-overriding-streaming.properties";


	private String streamingOverridingFilePath;
	protected File streamingOverridingFile;
	protected FileWriter streamingOverridingFileWriter;




	public EntititiesPropertiesConfigurationWriter()
	{
		logger = LoggerFactory.getLogger(EntititiesPropertiesConfigurationWriter.class);
		this.fileToConfigurePath = USER_HOME_DIR + ENTITIES_OVERRIDING_RELATIVE_PATH;
		this.streamingOverridingFilePath = USER_HOME_DIR + ENTITIES_OVERRIDING_STREAMING_RELATIVE_PATH;
	}


	@Override
	public boolean applyConfiguration() throws Exception {


		String line ="";

		String dataSourceName = gdsConfigurationState.getDataSourceName();

		String dataSourceList  = gdsConfigurationState.getExistingDataSources();
		//Configure the data source list
		line = String.format("leaf_entities=%s,%s",dataSourceList, dataSourceName);
		writeLineToFile(line, fileWriterToConfigure, true);
		writeLineToFile(line, streamingOverridingFileWriter, true);

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
		return null; //TODO change
	}
}
