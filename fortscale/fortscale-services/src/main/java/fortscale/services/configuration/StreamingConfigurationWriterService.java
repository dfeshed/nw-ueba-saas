package fortscale.services.configuration;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Set;

/**
 * Abstract implementation for streaming configuration services
 *
 * Created by idanp on 12/21/2015.
 */
public abstract class StreamingConfigurationWriterService extends ConfigurationWriterService {

	protected static  String FORTSCALE_CONFIGURATION_PREFIX  = "fortscale.events.entry";

	private static final String FORTSCALE_STREAMING_CONFIG_RELATIVE_PATH = "/fortscale/streaming/config/";

	protected static final String FORTSCALE_STREAMING_DIR_PATH = USER_HOME_DIR + FORTSCALE_STREAMING_CONFIG_RELATIVE_PATH;

	protected String dataSourceName;

	@Override
	public boolean init() {
		dataSourceName = gdsConfigurationState.getDataSourceName();

		return true;
	}

	public abstract boolean applyConfiguration() throws Exception;

	protected void writeMandatoryConfiguration(String taskName, String lastState, String outputTopic, String outputTopicEntry, boolean isGenericTopology) throws Exception{
		String line;

		line = String.format("# %s",dataSourceName);
		writeLineToFile(line, fileWriterToConfigure, true);

		if(!StringUtils.isEmpty(taskName)) {
			line = String.format("%s.name.%s_%s=%s_%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);
		}

		//data source
		if(!StringUtils.isEmpty(dataSourceName)) {
			line = String.format("%s.%s_%s.data.source=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName.toLowerCase());
			writeLineToFile(line, fileWriterToConfigure, true);
		}

		//last state
		if(!StringUtils.isEmpty(lastState)) {
			line = String.format("%s.%s_%s.last.state=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, lastState);
			writeLineToFile(line, fileWriterToConfigure, true);
		}

		if(!StringUtils.isBlank(outputTopic)) {
			if (isGenericTopology) {
				line = String.format("%s.%s_%s.%s=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, outputTopicEntry, outputTopic);
				writeLineToFile(line, fileWriterToConfigure, true);
			} else {

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic
			}
		}
	}

	@Override
	public Set<String> getAffectedConfigList() {
		return affectedConfigList;
	}

	protected void replaceValueInFile(String filePath ,String key,String replaceOrAddValue, Boolean override)
	{
		FileOutputStream out = null;
		try {
			File file = new File(filePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String currentLine;
			String newLine = "";


			//start read the file lines
			while ( (currentLine = br.readLine()) != null )
			{
				if (currentLine.contains(key))
				{
					if (override)
						currentLine = currentLine.substring(0,currentLine.indexOf(key))+replaceOrAddValue;
					else
						currentLine = currentLine+replaceOrAddValue;

				}
				newLine += currentLine + "\n";
			}

			out = new FileOutputStream(filePath);
			out.write(newLine.getBytes());
		}
		catch (Exception e)
		{
			logger.error("There was an exception during execution - {} ",e);
		}

		finally {
			try {
				if (out != null )
					out.close();
			}

			catch (Exception e)
			{
				logger.error("Fail to close the {} file  - {} ",filePath,e);
			}


		}

	}



}
