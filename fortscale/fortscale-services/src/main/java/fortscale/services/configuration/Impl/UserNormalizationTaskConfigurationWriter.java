package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Implementation of User normalization task configuration writer
 *
 * Created by idanp on 12/20/2015.
 */
public class UserNormalizationTaskConfigurationWriter extends StreamingConfigurationWriterService {

    public UserNormalizationTaskConfigurationWriter() {
        logger = LoggerFactory.getLogger(UserNormalizationTaskConfigurationWriter.class);
    }

    @Override
    public boolean init() {
        super.init();
        Boolean result;
        try {
            this.fileToConfigurePath = FORTSCALE_STREAMING_DIR_PATH + "username-normalization-tagging-task.properties";
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

            List<GDSEnrichmentDefinitionState.UserNormalizationState> userNormalizationStates = gdsConfigurationState.getEnrichmentDefinitionState().getUserNormalizationStates();

            for (GDSEnrichmentDefinitionState.UserNormalizationState userNormalizationState : userNormalizationStates) {
                String line = "";

                String taskName = userNormalizationState.getTaskName();
                String normalizationBasedField = userNormalizationState.getNormalizationBasedField();
                String domainValue = userNormalizationState.getDomainValue();
                String normalizedUserNameField = userNormalizationState.getNormalizedUserNameField();
                String normalizeServiceName = userNormalizationState.getNormalizeServiceName();
                String updateOnly = userNormalizationState.getUpdateOnly();
                String domainField = userNormalizationState.getDomainField();

                fileWriterToConfigure.write("\n");
                fileWriterToConfigure.write("\n");

                writeMandatoryConfiguration(taskName, userNormalizationState.getLastState(), userNormalizationState.getOutputTopic(), userNormalizationState.getOutputTopicEntry(), true);


                //User name field configuration
                line = String.format("%s.%s_%s.normalization.based.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, normalizationBasedField);
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
            }

            fileWriterToConfigure.flush();
            affectedConfigList.add(fileToConfigure.getAbsolutePath());
        }
        catch (Exception e)
        {
            logger.error("There was an exception during execution - {} ",e);
            return false;
        }

        return true;

    }
}
