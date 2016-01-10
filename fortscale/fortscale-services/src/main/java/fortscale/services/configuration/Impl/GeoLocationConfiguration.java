package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Implementation of Geo-location task configuration
 *
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
        Boolean result;
        try {
            this.fileToConfigurePath = FORTSCALE_STREAMING_DIR_PATH + "vpn-geolocation-session-update-task.properties";
            this.fileToConfigure = new File(this.fileToConfigurePath);
            this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
            result = true;
        } catch (Exception e) {
            logger.error("There was an exception during UserNormalizationTaskConfiguration init part execution - {} ", e.getMessage());
            System.out.println("There was an exception during execution please see more info at the log ");
            result = false;

        }
        return result;

    }

    @Override
    public boolean applyConfiguration() throws Exception {
        try {
            String line;

            fileWriterToConfigure.write("\n");
            fileWriterToConfigure.write("\n");

            List<GDSEnrichmentDefinitionState.GeoLocationState> geoLocationStates = gdsConfigurationState.getEnrichmentDefinitionState().getGeoLocationStates();

            for (GDSEnrichmentDefinitionState.GeoLocationState geoLocationState : geoLocationStates) {
                String taskName = geoLocationState.getTaskName();
                String ipField = geoLocationState.getIpField();
                String countryField = geoLocationState.getCountryField();
                String longitudeField = geoLocationState.getLongitudeField();
                String latitudeField = geoLocationState.getLatitudeField();
                String countryIsoCodeField = geoLocationState.getCountryIsoCodeField();
                String regionField = geoLocationState.getRegionField();
                String cityField = geoLocationState.getCityField();
                String ispField = geoLocationState.getIspField();
                String usageTypeField = geoLocationState.getUsageTypeField();
                boolean doSessionUpdateFlag = geoLocationState.isDoSessionUpdateFlag();
                boolean doDataBuckets = geoLocationState.isDoDataBuckets();
                boolean doGeoLocation = geoLocationState.isDoGeoLocation();

                writeMandatoryConfiguration(taskName, geoLocationState.getLastState(), geoLocationState.getOutputTopic(), geoLocationState.getOutputTopicEntry(), true);

                //source ip field
                line = String.format("%s.%s_%s.ip.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, ipField);
                writeLineToFile(line, fileWriterToConfigure, true);

                //country ip field
                line = String.format("%s.%s_%s.country.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, countryField);
                writeLineToFile(line, fileWriterToConfigure, true);

                //longtitude  field
                line = String.format("%s.%s_%s.longtitude.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, longitudeField);
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
                line = String.format("%s.%s_%s.doSessionUpdate=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doSessionUpdateFlag);
                writeLineToFile(line, fileWriterToConfigure, true);

                //put data bucket as false field
                line = String.format("%s.%s_%s.doDataBuckets=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doDataBuckets);
                writeLineToFile(line, fileWriterToConfigure, true);

                //put geo location as true field
                line = String.format("%s.%s_%s.doGeoLocation=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, doGeoLocation);
                writeLineToFile(line, fileWriterToConfigure, true);

                //partition field name  (today we use for all the username)
                line = String.format("%s.%s_%s.partition.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);

                //username
                line = String.format("%s.%s_%s.username.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);

                writeLineToFile("\n", fileWriterToConfigure, true);
                writeLineToFile("#############", fileWriterToConfigure, true);
            }

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
