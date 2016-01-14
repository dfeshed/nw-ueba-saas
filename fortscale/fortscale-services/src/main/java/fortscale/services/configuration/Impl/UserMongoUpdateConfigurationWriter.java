package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Implementation of User Mongo Update task configuration writer
 *
 * Created by idanp on 12/21/2015.
 */
public class UserMongoUpdateConfigurationWriter extends StreamingConfigurationWriterService {

	public UserMongoUpdateConfigurationWriter()
	{

		logger = LoggerFactory.getLogger(UserMongoUpdateConfigurationWriter.class);
	}

	@Override
	public boolean init() {
		super.init();
		Boolean result;
		try {
			this.fileToConfigurePath = FORTSCALE_STREAMING_DIR_PATH + "user-mongo-update-task.properties";
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during UserNormalizationTaskConfigurationWriter init part execution - {} ", e.getMessage());
			System.out.println("There was an exception during execution please see more info at the log ");
			result = false;
		}

		return result;
	}

	@Override
	public boolean applyConfiguration() throws Exception {
        try {
            String line;
            String statusFieldName="";
            String successValue="";

			fileWriterToConfigure.write("\n");
			fileWriterToConfigure.write("\n");

            GDSEnrichmentDefinitionState.UserMongoUpdateState userMongoUpdateState = gdsConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState();

            String taskName = userMongoUpdateState.getTaskName();

            writeMandatoryConfiguration(taskName, userMongoUpdateState.getLastState(), userMongoUpdateState.getOutputTopic(), userMongoUpdateState.getOutputTopicEntry(), true);

            line = String.format("%s.%s_%s.classifier=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName.toLowerCase());
            writeLineToFile(line, fileWriterToConfigure, true);

            if (userMongoUpdateState.isAnyRow()) {
                line = String.format("%s.%s_%s.success.field=#AnyRow#", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
                writeLineToFile(line, fileWriterToConfigure, true);
                line = String.format("%s.%s_%s.success.value=#NotRelevant#", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
                writeLineToFile(line, fileWriterToConfigure, true);
            } else {
                line = String.format("%s.%s_%s.success.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, statusFieldName);
                writeLineToFile(line, fileWriterToConfigure, true);
                line = String.format("%s.%s_%s.success.value=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, successValue);
                writeLineToFile(line, fileWriterToConfigure, true);
            }

            //logusername
            line = String.format("%s.%s_%s.logusername.field=username", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //normalized_username
            line = String.format("%s.%s_%s.username.field=normalized_username", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);


            //TODO - NOT SURE THIS FILED IS NEEDED , NET TO VALIDATE AND IF NOT TO REMOVE IT
            line = String.format("%s.%s_%s.UserMongoUpdateStreamTask.updateOnly=false", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);

            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("#############", fileWriterToConfigure, true);


            fileWriterToConfigure.flush();
            affectedConfigList.add(fileToConfigure.getAbsolutePath());
        }
        catch (Exception e)
        {
            logger.error("There was an exception during execution - {} ",e.getMessage()!=null ? e.getMessage() : e.getCause().getMessage());
            return false;
        }


		return true;



	}
}
