package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.StreamingConfigurationService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by idanp on 12/20/2015.
 */
public class UserNormalizationTaskConfiguration extends StreamingConfigurationService {




	public UserNormalizationTaskConfiguration() {


		logger = LoggerFactory.getLogger(UserNormalizationTaskConfiguration.class);

	}




	@Override
	public Boolean init() {

		super.init();
		Boolean result = false;
		try {
			this.fileToConfigurePath =this.fileToConfigurePath+"username-normalization-tagging-task.properties";
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
	public Boolean applyConfiguration() throws Exception {

        try {
            String line = "";
			ConfigurationParam result = getParamConfiguration(configurationParams,"userNameField");
            String userNameField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"domainFieldName");
            String domainField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"domainValue");
            String domainValue = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"normalizedUserNameField");
            String normalizedUserNameField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"normalizeSservieName");
            String normalizeServiceName = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"updateOnlyFlag");
            String updateOnly = result != null ? result.getParamValue() : null;

            System.out.println(String.format("Going to configure the Normalized Username and tagging task for %s", dataSourceName));

            fileWriterToConfigure.write("\n");
            fileWriterToConfigure.write("\n");


            mandatoryConfiguration();


            //User name field configuration
            line = String.format("%s.%s_%s.username.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, userNameField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //Domain field name
            line = String.format("%s.%s_%s.domain.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, domainField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //Domain value
            line = String.format("%s.%s_%s.domain.fake=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, domainValue!=null ? domainValue : "");
            writeLineToFile(line, fileWriterToConfigure, true);

            //Normalized user name value
            line = String.format("%s.%s_%s.normalizedusername.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, normalizedUserNameField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //Partition Field Name
            //TODO - TOday its user name for all cases , if it will be change need to put it out to configuration
            line = String.format("%s.%s_%s.partition.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //Service name
            line = String.format("%s.%s_%s.normalization.service=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, normalizeServiceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("%s.%s_%s.updateOnly=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, updateOnly);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("%s.%s_%s.classifier=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);


            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("#############", fileWriterToConfigure, true);


            fileWriterToConfigure.flush();
        }
        catch (Exception e)
        {
            logger.error("There was an exception during execution - {} ",e);
            return false;
        }

		return true;

	}



}
