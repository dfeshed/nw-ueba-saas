package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Implementation of IP Resolving task configuration writer
 *
 * Created by idanp on 12/21/2015.
 */
public class IpResolvingTaskConfigurationWriter extends StreamingConfigurationWriterService {

    public IpResolvingTaskConfigurationWriter()
    {

        logger = LoggerFactory.getLogger(IpResolvingTaskConfigurationWriter.class);
    }


    @Override
    public boolean init() {
        super.init();
        Boolean result;
        try {
            this.fileToConfigurePath = FORTSCALE_STREAMING_DIR_PATH + "ip-resolving-task.properties";
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

            fileWriterToConfigure.write("\n");
            fileWriterToConfigure.write("\n");
            List<GDSEnrichmentDefinitionState.IPResolvingState> ipResolvingStates = gdsConfigurationState.getEnrichmentDefinitionState().getIpResolvingStates();

            for (GDSEnrichmentDefinitionState.IPResolvingState ipResolvingState : ipResolvingStates) {
                String taskName = ipResolvingState.getTaskName();
                String ipField = ipResolvingState.getIpField();
                String hostField = ipResolvingState.getHostField();
                boolean restrictToAD = ipResolvingState.isRestrictToAD();
                boolean shortNameUsage = ipResolvingState.isShortNameUsage();
                boolean removeLastDotUsage = ipResolvingState.isRemoveLastDotUsage();
                boolean dropOnFailUsage = ipResolvingState.isDropOnFailUsage();
                boolean overrideIpWithHostNameUsage = ipResolvingState.isOverrideIpWithHostNameUsage();

                writeMandatoryConfiguration(ipResolvingState.getTaskName(), ipResolvingState.getLastState(), ipResolvingState.getOutputTopic(), ipResolvingState.getOutputTopicEntry(), true);

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

                line = String.format("%s.%s_%s.restrictToADName=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, restrictToAD);
                writeLineToFile(line, fileWriterToConfigure, true);

                //short name

                line = String.format("%s.%s_%s.shortName=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, shortNameUsage);
                writeLineToFile(line, fileWriterToConfigure, true);

                //Remove last Dot

                line = String.format("%s.%s_%s.isRemoveLastDot=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, removeLastDotUsage);
                writeLineToFile(line, fileWriterToConfigure, true);

                //Drop When Fail

                line = String.format("%s.%s_%s.dropWhenFail=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dropOnFailUsage);
                writeLineToFile(line, fileWriterToConfigure, true);


                //Override IP with Hostname

                line = String.format("%s.%s_%s.overrideIPWithHostname=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, overrideIpWithHostNameUsage);
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
