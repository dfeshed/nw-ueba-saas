package fortscale.services.configuration;

import org.apache.commons.lang.StringUtils;

/**
 * Created by idanp on 12/21/2015.
 */
public abstract class StreamingConfigurationService extends ConfigurationService {

	protected static final String FORTSCALE_CONFIGURATION_PREFIX  = "fortscale.events.entry";
	protected String taskName;
	protected Boolean topolegyResult;
	protected String lastState;
	protected String outPutTopic;
	protected String dataSourceName;
	protected String outPutTopicEntry;

	@Override
	public boolean init() {
		this.fileToConfigurePath = this.root+"/fortscale/streaming/config/";
		taskName = configurationParams.get("taskName").getParamValue();
		topolegyResult = configurationParams.get("topologyFlag").getParamFlag();
		lastState  = configurationParams.get("lastState").getParamValue();
		outPutTopic = configurationParams.get("outPutTopic").getParamValue();
		dataSourceName = configurationParams.get("dataSourceName").getParamValue();
		outPutTopicEntry = "output.topic";
		return true;
	}

	public abstract boolean applyConfiguration() throws Exception;

	protected void mandatoryConfiguration () throws Exception{
		String line = "";


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


		if(!StringUtils.isBlank(outPutTopic)) {
			//GDS general topology
			if (topolegyResult) {
				line = String.format("%s.%s_%s.%s=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,outPutTopicEntry,outPutTopic);
				writeLineToFile(line, fileWriterToConfigure, true);
			} else {

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}
		}
	}


}
