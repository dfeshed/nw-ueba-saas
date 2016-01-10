package fortscale.services.configuration;

import org.apache.commons.lang.StringUtils;

/**
 * Abstract implementation for streaming configuration services
 *
 * Created by idanp on 12/21/2015.
 */
public abstract class StreamingConfigurationService extends ConfigurationService {

	protected static final String FORTSCALE_CONFIGURATION_PREFIX  = "fortscale.events.entry";
	protected String dataSourceName;

	@Override
	public boolean init() {
		this.fileToConfigurePath = USER_HOME_DIR + "/fortscale/streaming/config/";
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
}
