package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.StreamingConfigurationService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by idanp on 12/21/2015.
 */
public class IpResolvingTaskConfiguration extends StreamingConfigurationService {




	public IpResolvingTaskConfiguration ()
	{

		logger = LoggerFactory.getLogger(IpResolvingTaskConfiguration.class);
	}


	@Override
	public boolean init() {
		super.init();
		Boolean result = false;
		try {
			this.fileToConfigurePath = this.fileToConfigurePath+"ip-resolving-task.properties";
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
	public boolean applyConfiguration() throws Exception {
        String outPutTopicEntry = "output.topic";

        try {
            String line = "";
			ConfigurationParam result = getParamConfiguration(configurationParams,"restrictToAD");
            Boolean restrictToAD = result != null ? result.getParamFlag() : null;

			result = getParamConfiguration(configurationParams,"restrictToAD");
            Boolean shortNameUsage = result != null ? result.getParamFlag() : null;

			getParamConfiguration(configurationParams,"restrictToAD");
            Boolean removeLastDotUsage = result != null ? result.getParamFlag() : null;

			getParamConfiguration(configurationParams,"dropOnFailUsage");
            Boolean dropOnFailUsage = result != null ? result.getParamFlag() : null;

			getParamConfiguration(configurationParams,"overrideIpWithHostNameUsage");
            Boolean overrideIpWithHostNameUsage = result != null ? result.getParamFlag() : null;

			getParamConfiguration(configurationParams,"ipField");
            String ipField = result != null ? result.getParamValue() : null;

			getParamConfiguration(configurationParams,"host");
            String hostField = result != null ? result.getParamValue() : null;

			fileWriterToConfigure.write("\n");
			fileWriterToConfigure.write("\n");

            writeMandatoryConfiguration();

            //partition field name  (today we use for all the username)
            line = String.format("%s.%s_%s.partition.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //ip field
            line = String.format("%s.%s_%s.ip.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, ipField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //hostname
            line = String.format("%s.%s_%s.host.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hostField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //time stamp
            line = String.format("%s.%s_%s.timestamp.field=${impala.data.%s.table.field.epochtime}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);


            //restric to AD

            line = String.format("%s.%s_%s.restrictToADName=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, restrictToAD.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //short name

            line = String.format("%s.%s_%s.shortName=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, shortNameUsage.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //Remove last Dot

            line = String.format("%s.%s_%s.isRemoveLastDot=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, removeLastDotUsage.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //Drop When Fail

            line = String.format("%s.%s_%s.dropWhenFail=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dropOnFailUsage.toString());
            writeLineToFile(line, fileWriterToConfigure, true);


            //Override IP with Hostname

            line = String.format("%s.%s_%s.overrideIPWithHostname=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, overrideIpWithHostNameUsage.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("#############", fileWriterToConfigure, true);


            fileWriterToConfigure.flush();
        }

        catch (Exception e)
        {
            logger.error("There was an exception during execution - {} ",e.getMessage()!=null ? e.getMessage() : e.getCause().getMessage());
            return false;
        }


		return true;


	}
}
