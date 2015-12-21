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
public class ComputerTaggingClassConfiguration extends StreamingConfigurationService {


	public ComputerTaggingClassConfiguration (Map<String, ConfigurationParam> params)
	{
		this.configurationParams = params;
		logger = LoggerFactory.getLogger(ComputerTaggingClassConfiguration.class);
	}

	@Override
	public Boolean Init() {
		super.Init();
		Boolean result = false;
		try {
			this.fileToConfigurePath = this.fileToConfigurePath+"computer-tagging-clustering-task.properties";
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

		String sourceHostField = configurationParams.get("srcHost").getParamValue();
		String targetHostField = configurationParams.get("dstHost").getParamValue();
		String srcMachineClassifier = configurationParams.get("srcMachineClassifier").getParamValue();
		String srcClusteringField = configurationParams.get("srcClusteringField").getParamValue();
		Boolean createNewComputerFlag = configurationParams.get("createNewComputerFlag").getParamFlag();
		String dstMachineClassifier = configurationParams.get("dstMachineClassifier").getParamValue();
		String dstClusteringField = configurationParams.get("dstClusteringField").getParamValue();

		mandatoryConfiguration();

		//partition field name  (today we use for all the username)
		line = String.format("%s.%s_%s.partition.field=${impala.data.%s.table.field.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
		writeLineToFile(line, fileWriterToConfigure, true);

		if (configurationParams.get("sourceMachineNormalizationFlag").getParamFlag()) {


			//source name
			line = String.format("%s.%s_%s.source.hostname.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, sourceHostField);
			writeLineToFile(line, fileWriterToConfigure, true);

			//classification
			line = String.format("%s.%s_%s.source.classification.field=${impala.data.%s.table.field.src_class}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, srcMachineClassifier);
			writeLineToFile(line, fileWriterToConfigure, true);

			//Normalized_src_machine (clustering)
			line = String.format("%s.%s_%s.source.clustering.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, srcClusteringField);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.source.create-new-computer-instances=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, createNewComputerFlag.toString());
			writeLineToFile(line, fileWriterToConfigure, true);


		}
		if (configurationParams.get("targetMachineNormalizationFlag").getParamFlag()) {


			//source name
			line = String.format("%s.%s_%s.destination.hostname.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, targetHostField);
			writeLineToFile(line, fileWriterToConfigure, true);

			//classification
			line = String.format("%s.%s_%s.destination.classification.field=${impala.data.%s.table.field.src_class}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dstMachineClassifier);
			writeLineToFile(line, fileWriterToConfigure, true);

			//Normalized_src_machine (clustering)
			line = String.format("%s.%s_%s.destination.clustering.field=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dstClusteringField);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.destination.create-new-computer-instances=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, createNewComputerFlag.toString());
			writeLineToFile(line, fileWriterToConfigure, true);

			// configure the is sensitive machine field
			line = String.format("%s.%s_%S.destination.is-sensitive-machine.field=${impala.data.%s.table.field.is_sensitive_machine}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);


		}

		writeLineToFile("\n", fileWriterToConfigure, true);
		writeLineToFile("#############", fileWriterToConfigure, true);


		fileWriterToConfigure.flush();

		return true;
	}


}
