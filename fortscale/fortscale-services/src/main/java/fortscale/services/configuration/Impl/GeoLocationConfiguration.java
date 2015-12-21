package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.StreamingConfigurationService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Created by idanp on 12/21/2015.
 */
public class GeoLocationConfiguration extends StreamingConfigurationService {

	public GeoLocationConfiguration (Map<String, ConfigurationParam> params)
	{
		this.configurationParams = params;
		logger = LoggerFactory.getLogger(GeoLocationConfiguration.class);
	}

	@Override
	public Boolean Init() {
		super.Init();
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
	public Boolean Configure() throws Exception {
        try {
            String line = "";
            String ipField = configurationParams.get("ipField").getParamValue();
            String countryField = configurationParams.get("countryField").getParamValue();
            String longtitudeField = configurationParams.get("longtitudeField").getParamValue();
            String latitudeField = configurationParams.get("latitudeField").getParamValue();
            String countryIsoCodeField = configurationParams.get("countryIsoCodeField").getParamValue();
            String regionField = configurationParams.get("regionField").getParamValue();
            String cityField = configurationParams.get("cityField").getParamValue();
            String ispField = configurationParams.get("ispField").getParamValue();
            String usageTypeField = configurationParams.get("usageTypeField").getParamValue();
            Boolean doSessionUpdateFlag = configurationParams.get("doSessionUpdateFlag").getParamFlag();
            Boolean doDataBuckets = configurationParams.get("doDataBuckets").getParamFlag();
            Boolean doGeoLocation = configurationParams.get("doGeoLocation").getParamFlag();


            mandatoryConfiguration();

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
            logger.error("There was an exception during execution - {} ",e.getMessage());
            return false;
        }

		return true;
	}
}
