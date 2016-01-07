package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.StreamingConfigurationService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by idanp on 12/21/2015.
 */
public class GeoLocationConfiguration extends StreamingConfigurationService {

	public GeoLocationConfiguration ()
	{

		logger = LoggerFactory.getLogger(GeoLocationConfiguration.class);
	}

	@Override
	public boolean init() {
		super.init();
		Boolean result = false;
		try {
			this.fileToConfigurePath = this.fileToConfigurePath+"vpn-geolocation-session-update-task.properties";
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

			ConfigurationParam result = getParamConfiguration(configurationParams,"ipField");
            String ipField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"countryField");
            String countryField = result != null ? result.getParamValue() : null;


			result = getParamConfiguration(configurationParams,"longtitudeField");
            String longtitudeField = result != null ? result.getParamValue() : null;


			result = getParamConfiguration(configurationParams,"latitudeField");
            String latitudeField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"countryIsoCodeField");
            String countryIsoCodeField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"regionField");
            String regionField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"cityField");
            String cityField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"ispField");
            String ispField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"usageTypeField");
            String usageTypeField = result != null ? result.getParamValue() : null;

			result = getParamConfiguration(configurationParams,"doSessionUpdateFlag");
            Boolean doSessionUpdateFlag = result != null ? result.getParamFlag() : null;

			result = getParamConfiguration(configurationParams,"doDataBuckets");
            Boolean doDataBuckets = result != null ? result.getParamFlag() : null;

			result = getParamConfiguration(configurationParams,"doGeoLocation");
            Boolean doGeoLocation = result != null ? result.getParamFlag() : null;


			fileWriterToConfigure.write("\n");
			fileWriterToConfigure.write("\n");

            writeMandatoryConfiguration();

            //source ip field
            line = String.format("%s.%s_%s.ip.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, ipField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //country ip field
            line = String.format("%s.%s_%s.country.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, countryField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //longtitude  field
            line = String.format("%s.%s_%s.longtitude.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, longtitudeField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //latitude  field
            line = String.format("%s.%s_%s.latitude.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, latitudeField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //countryIsoCode field
            line = String.format("%s.%s_%s.countryIsoCode.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, countryIsoCodeField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //region  field
            line = String.format("%s.%s_%s.region.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, regionField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //city field
            line = String.format("%s.%s_%s.city.field=%S", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, cityField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //isp field
            line = String.format("%s.%s_%s.isp.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, ispField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //usageType field
            line = String.format("%s.%s_%s.usageType.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, usageTypeField);
            writeLineToFile(line, fileWriterToConfigure, true);

            //put session update configuration as false  field
            line = String.format("%s.%s_%s.doSessionUpdate=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doSessionUpdateFlag.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //put data bucket as false field
            line = String.format("%s.%s_%s.doDataBuckets=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doDataBuckets.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //put geo location as true field
            line = String.format("%s.%s_%s.doGeoLocation=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doGeoLocation.toString());
            writeLineToFile(line, fileWriterToConfigure, true);

            //partition field name  (today we use for all the username)
            line = String.format("%s.%s_%s.partition.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //username
            line = String.format("%s.%s_%s.username.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
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
