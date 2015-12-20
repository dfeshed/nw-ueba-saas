package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Created by idanp on 12/20/2015.
 */
public class UserNormalizationTaskConfiguration extends ConfigurationService {

	private static final String FORTSCALE_CONFIGURATION_PREFIX  = "fortscale.events.entry";


	public UserNormalizationTaskConfiguration(Map<String, ConfigurationParam> params) {

		this.configurationParams = params;
		logger = LoggerFactory.getLogger(UserNormalizationTaskConfiguration.class);

	}


	@Override
	public Boolean Init() {
		Boolean result = false;
		try {
			this.fileToConfigurePath = this.root+"fortscale/streaming/config/username-normalization-tagging-task.properties";
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during UserNormalizationTaskConfiguration init part execution - {} ", e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			result = false;

		}
		return result;
	}

	@Override
	public Boolean Configure() throws Exception {

		String line = "";
		Boolean topolegyResult = configurationParams.get("topologyFlag").getParamFlag();
		String lastState  = configurationParams.get("lastState").getParamValue();
        String taskName = configurationParams.get("taskName").getParamValue();
        String outPutTopic = configurationParams.get("outPutTopic").getParamValue();
        String userNameField = configurationParams.get("userNameField").getParamValue();
        String domainField = configurationParams.get("domainFieldName").getParamValue();
        String domainValue = configurationParams.get("domainValue").getParamValue();
        String normalizedUserNameField = configurationParams.get("normalizedUserNameField").getParamValue();
        String normalizeServiceName = configurationParams.get("normalizeSservieName").getParamValue();
        String updateOnly = configurationParams.get("updateOnlyFlag").getParamValue();


		String dataSourceName = configurationParams.get("dataSourceName").getParamValue();
		System.out.println(String.format("Going to configure the Normalized Username and tagging task for %s",dataSourceName));

		fileWriterToConfigure.write("\n");
		fileWriterToConfigure.write("\n");


		line = String.format("# %s",dataSourceName);
		writeLineToFile(line, fileWriterToConfigure, true);

        configureTaskMandatoryConfiguration(fileWriterToConfigure,topolegyResult,taskName,lastState,outPutTopic);

        //User name field configuration
        line = String.format("%s.%s_%s.username.field=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,userNameField);
        writeLineToFile(line, fileWriterToConfigure, true);

        //Domain field name
        line = String.format("%s.%s_%s.domain.field=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,domainField);
        writeLineToFile(line, fileWriterToConfigure, true);

        //Domain value
        line = String.format("%s.%s_%s.domain.fake=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,domainValue);
        writeLineToFile(line, fileWriterToConfigure, true);

        //Normalized user name value
        line = String.format("%s.%s_%s.normalizedusername.field=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,normalizedUserNameField);
        writeLineToFile(line, fileWriterToConfigure, true);

        //Partition Field Name
        //TODO - TOday its user name for all cases , if it will be change need to put it out to configuration
        line = String.format("%s.%s_%s.partition.field=username",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName);
        writeLineToFile(line, fileWriterToConfigure, true);

        //Service name
        line = String.format("%s.%s_%s.normalization.service=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,normalizeServiceName);
        writeLineToFile(line, fileWriterToConfigure, true);

        line = String.format("%s.%s_%s.updateOnly=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,updateOnly);
        writeLineToFile(line, fileWriterToConfigure, true);

        line = String.format("%s.%s_%s.classifier=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,dataSourceName);
        writeLineToFile(line, fileWriterToConfigure, true);



        writeLineToFile("\n", fileWriterToConfigure, true);
        writeLineToFile("#############", fileWriterToConfigure, true);

		return true;

	}

	private void configureTaskMandatoryConfiguration(FileWriter taskPropertiesFileWriter ,Boolean topolegyResult, String name,String lastState,String outputTopic) throws Exception{
		String line ="";
		String dataSourceName = configurationParams.get("dataSourceName").getParamValue();
		//name
		line = String.format("%s.name.%s_%s=%s_%s",FORTSCALE_CONFIGURATION_PREFIX,dataSourceName,name,dataSourceName,name);
		writeLineToFile(line, taskPropertiesFileWriter, true);

		//data source
		line = String.format("%s.%s_%s.data.source=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,name, dataSourceName.toLowerCase());
		writeLineToFile(line, taskPropertiesFileWriter, true);

		//last state
		line = String.format("%s.%s_%s.last.state=%s",FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,name,lastState);
		writeLineToFile(line, taskPropertiesFileWriter, true);


		if(!StringUtils.isBlank(outputTopic)) {
			//GDS general topology
			if (topolegyResult) {
				line = String.format("%s.%s_%s.output.topic=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, name, outputTopic);
				writeLineToFile(line, taskPropertiesFileWriter, true);
			} else {

				System.out.println("Not supported yet via  this configuration tool ");
				//TODO - Need to add the topic configuration  also for task.inputs and fortscale.events.entry.<dataSource>_UsernameNormalizationAndTaggingTask.output.topic

			}
		}
	}


}
