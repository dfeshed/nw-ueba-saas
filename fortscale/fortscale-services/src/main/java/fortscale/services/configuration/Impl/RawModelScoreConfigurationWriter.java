package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import fortscale.services.configuration.gds.state.GDSRAWDataModelAndScoreState;
import fortscale.services.configuration.gds.state.GDSSchemaDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Implementation of Raw model and Score configuration writer
 * Created by idanp on 12/29/2015.
 */
public class RawModelScoreConfigurationWriter extends StreamingConfigurationWriterService {

	public RawModelScoreConfigurationWriter()
	{

		logger = LoggerFactory.getLogger(RawModelScoreConfigurationWriter.class);
	}

	@Override
	public boolean init() {
		super.init();
		Boolean result = false;
		try {
			this.fileToConfigurePath = this.fileToConfigurePath+"raw-events-prevalence-stats-task.properties";
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during RawModelScoreConfigurationWriter init part execution - {} ", e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			result = false;

		}
		return result;

	}



	@Override
	public boolean applyConfiguration() throws Exception {
		try {

			String line="";
			String modelTypeClass = "";

			GDSSchemaDefinitionState schemaDefinitionState = gdsConfigurationState.getSchemaDefinitionState();
			GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState = gdsConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState();
			GDSEnrichmentDefinitionState.ComputerTaggingState computerTaggingState = gdsConfigurationState.getEnrichmentDefinitionState().getComputerTaggingState();
			GDSRAWDataModelAndScoreState rawDataModelAndScoreState = gdsConfigurationState.getRawDataModelAndScoreState();

			//get state params
			String discriminatorsFields = hdfsWriterState.getDiscriminatorsFields();
			String sourceMachineFieldName = computerTaggingState.getSourceHost();
			String destinationMachineFieldName = computerTaggingState.getTargetHost();
			String srcClusteringField = computerTaggingState.getSrcClusteringField();
			String dstClusteringField = computerTaggingState.getDstClusteringField();

			Map<String,String> scoresFieldMap = rawDataModelAndScoreState.getScoresFieldMap();
			Map<String,String> additionalScoreFeldsMap = rawDataModelAndScoreState.getAdditionalScoreFeldsMap();
			Map<String,String> additionalFieldsMap = rawDataModelAndScoreState.getAdditionalFieldsMap();
			Map<String,String> additionalFiledToScoreFieldMap = rawDataModelAndScoreState.getAdditionalFiledToScoreFieldMap();


			boolean sourceMachienFlag = rawDataModelAndScoreState.getSourceMachienFlag();
			boolean destMachienFlag =  rawDataModelAndScoreState.getDestMachienFlag();
			boolean countryToScoreFlag = rawDataModelAndScoreState.getCountryToScoreFlag();
			boolean dateTimeToScoreFlag = rawDataModelAndScoreState.getDateTimeToScoreFlag();
			boolean actionTypeToScoreFlag = rawDataModelAndScoreState.getActionTypeToScoreFlag();

			String  taskName = rawDataModelAndScoreState.getTaskName();

			String dataSourcesKeyString = rawDataModelAndScoreState.getDataSourcesConfigurationKey();


			// add the new data source to the data source list
			replaceValueInFile(this.fileToConfigurePath, dataSourcesKeyString, ","+this.dataSourceName, false);

			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# Fortscale specific task config parameters - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);


			writeLineToFile("\n", fileWriterToConfigure, true);
			writeLineToFile("\n", fileWriterToConfigure, true);

			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("# %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);

			writeMandatoryConfiguration(taskName,rawDataModelAndScoreState.getLastState(),rawDataModelAndScoreState.getOutputTopic(),rawDataModelAndScoreState.getOutputTopicEntry(),true);

			//user name field
			line = String.format("%s.%s_%s.username.field=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			//user name field
			line = String.format("%s.%s_%s.source.type=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);

			//user name field
			line = String.format("%s.%s_%s.entity.type=event", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			//user name field
			line = String.format("%s.%s_%s.timestamp.field=date_time_unix", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			//user name field
			line = String.format("%s.%s_%s.discriminator.fields=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,discriminatorsFields);
			writeLineToFile(line, fileWriterToConfigure, true);

			//user name field
			line = String.format("%s.%s_%s.store.name=%s-prevalence-stats", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);


			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# Feature Extractor - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);



			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("#  %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);


			if (sourceMachienFlag) {
				//source machien feature extractor
				line = String.format("%s.%s_%s.feature.extractor.src_machine_cluster.class.json={\"type\":\"event_feature_extractor\",\"originalFieldName\":\"%s\",\"featureAdjustor\":{\"type\":\"conditional_pattern_replacement_feature_adjuster\",\"pattern\":\"[0-9]\",\"replacement\":\"\",\"postReplacementCondition\":\"(.*[a-zA-Z]){5}.*\"}}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, sourceMachineFieldName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}


			if (destMachienFlag) {
				//dest  machien feature extractor
				line = String.format("%s.%s_%s.feature.extractor.dst_machine_cluster.class.json={\"type\":\"event_feature_extractor\",\"originalFieldName\":\"%s\",\"featureAdjustor\":{\"type\":\"conditional_pattern_replacement_feature_adjuster\",\"pattern\":\"[0-9]\",\"replacement\":\"\",\"postReplacementCondition\":\"(.*[a-zA-Z]){5}.*\"}}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, destinationMachineFieldName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}

			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# Local Prevalence Models - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);


			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("#  %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);



			//time gap for update model
			line = String.format("%s.%s_%s.time.gap.for.model.updates=14400000", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			//models order
			String modelNames=dataSourceName+"user";
			String scorersName = "";
			if (sourceMachienFlag) {
				modelNames = modelNames + "," + dataSourceName + "SrcComputer";
				scorersName = scorersName + "hostnameScorer,";

				//Source machine modeling in user context
				line = String.format("%s.%s_%s.model.%suser.fields.src_machine_cluster.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);

				//the source machine as a context
				line = String.format("%s.%s_%s.model.%sSrcComputer.context.fieldname=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName,taskName,dataSourceName,srcClusteringField);
				writeLineToFile(line, fileWriterToConfigure, true);

				//the source machine as a context
				line = String.format("%s.%s_%s.model.sshSrcComputer.fields.normalized_username.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);


			}
			if(destMachienFlag) {
				modelNames = modelNames + "," + dataSourceName + "DstComputer";
				scorersName = scorersName + "destScorer,";
				//dest machine modeling in user context
				line = String.format("%s.%s_%s.model.%suser.fields.dst_machine_cluster.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);

				//the source machine as a context
				line = String.format("%s.%s_%s.model.%sDstComputer.context.fieldname=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName,dstClusteringField);
				writeLineToFile(line, fileWriterToConfigure, true);

				//the source machine as a context
				line = String.format("%s.%s_%s.model.sshDstComputer.fields.normalized_username.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}
			if(countryToScoreFlag){
				modelNames = modelNames + "," + dataSourceName + "city";
				scorersName = scorersName + "countryLimitScorer,";

				//user model - country
				line = String.format("%s.%s_%s.model.%suser.fields.country.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);

				//city model (global)
				line = String.format("%s.%s_%s.model.%scity.context.fieldname=city", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);

				//city model replacement (global)
				line = String.format("%s.%s_%s.model.%scity.context.fieldname.optional.replacement=source_ip", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);


				//city model replacement (global)
				line = String.format("%s.%s_%s.model.%scity.fields.normalized_username.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);


			}

			line = String.format("%s.%s_%s.models.names.order=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,modelNames);
			writeLineToFile(line, fileWriterToConfigure, true);


			//user model - context field name
			line = String.format("%s.%s_%s.model.%s.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);


			if (dateTimeToScoreFlag) {
				//user model - time model
				scorersName = scorersName + "dateTimeScorer,";
				line = String.format("%s.%s_%s.model.%suser.fields.date_time_unix.model=fortscale.ml.model.prevalance.field.DailyTimeStreamModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}

			if (actionTypeToScoreFlag) {
				scorersName = scorersName + "actionTypeScorer,";
				//user model - time model
				line = String.format("%s.%s_%s.model.%suser.fields.action_type.model=fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}

			//Get the additional field to model on
			for (Map.Entry<String,String> entry : additionalScoreFeldsMap.entrySet()){

				String scoreFiledName = entry.getKey();
				String additionalFileForModel = additionalFiledToScoreFieldMap.get(scoreFiledName);
				String dataType = additionalFieldsMap.get(additionalFileForModel);


				if (dataType.equals("STRING"))
				{
					modelTypeClass = "fortscale.ml.model.prevalance.field.StringCaseInsensitiveValuesCalibrationModel";
				}
				else if (dataType.equals("TIMESTAMP")){
					modelTypeClass = "fortscale.ml.model.prevalance.field.DailyTimeStreamModel";
				}
				else{
					modelTypeClass = "fortscale.ml.model.prevalance.field.ContinuousValuesFieldModel";

				}
				//user model - additional fields
				line = String.format("%s.%s_%s.model.%suser.fields.%s.model=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName,additionalFileForModel,modelTypeClass);
				writeLineToFile(line, fileWriterToConfigure, true);

				scorersName = scorersName + String.format("%sScorer,",additionalFileForModel);

			}

			//remove last ',' from scorer list
			scorersName = scorersName.substring(0,scorersName.length()-1);




			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# Scorers - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);

			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("#  %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.scorers=eventscorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.score.eventscorer.output.field.name=eventscore", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.score.eventscorer.scorer=pareto-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("%s.%s_%s.score.eventscorer.highest.score.weight=0.8", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);


			//configure the scorer list
			line = String.format("%s.%s_%s.score.eventscorer.scorers=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,scorersName);
			writeLineToFile(line, fileWriterToConfigure, true);


			//date_time_scorer
			if(dateTimeToScoreFlag) {
				line = String.format("%s.%s_%s.score.dateTimeScorer.output.field.name=date_time_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.score.dateTimeScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.score.dateTimeScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.score.dateTimeScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.score.dateTimeScorer.%suser.fieldname=date_time_unix", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.num.of.samples.to.influence.enough=100", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.dateTimeScorer.use.certainty.to.calculate.score=true", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);

			}

			//country socrer
			if(countryToScoreFlag) {
				line = String.format("%s.%s_%s.score.countryLimitScorer.output.field.name=country_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryLimitScorer.scorer=field-value-score-reducer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryLimitScorer.base.scorer=countryScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryLimitScorer.limiters={\"limiters\":[]}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryScorer.output.field.name=countryScoreAfterGlobal", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryScorer.scorer=reducting-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryScorer.main.scorer=countryMainScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryScorer.reducting.scorer=countryReductingScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryScorer.reducting.weight=0.5", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.output.field.name=country_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.%suser.fieldname=country", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.num.of.samples.to.influence.enough=50", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryMainScorer.use.certainty.to.calculate.score=true", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.output.field.name=country_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.scorer=discreet-model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.model.name=%scity", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.%scity.context.fieldname=city", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.%scity.context.fieldname.optional.replacement=${impala.data.%s.table.field.source}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.%scity.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.min=5", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);

				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.countryReductingScorer.discreet.values.to.influence.enough=10", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);


			}

			if(destMachienFlag)
			{
				line = String.format("%s.%s_%s.score.normalizedDstMachineScorer.output.field.name=normalized_dst_machine_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedDstMachineScorer.scorer=reducting-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedDstMachineScorer.main.scorer=destMainScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedDstMachineScorer.reducting.scorer=destReductingScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedDstMachineScorer.reducting.weight=0.5", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destMainScorer.output.field.name=destMainScore", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destMainScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destMainScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destMainScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destMainScorer.%suser.fieldname=dst_machine_cluster", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destReductingScorer.output.field.name=destReductingScore", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destReductingScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destReductingScorer.model.name=%sDstComputer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destReductingScorer.%sDstComputer.context.fieldname=normalized_dst_machine", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.destReductingScorer.%sDstComputer.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);


			}

			if (sourceMachienFlag)
			{
				line = String.format("%s.%s_%s.score.normalizedSrcMachineScorer.output.field.name=normalized_src_machine_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedSrcMachineScorer.scorer=reducting-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedSrcMachineScorer.main.scorer=srcMainScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedSrcMachineScorer.reducting.scorer=srcReductingScorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.normalizedSrcMachineScorer.reducting.weight=0.5", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcMainScorer.output.field.name=srcMainScore", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcMainScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcMainScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcMainScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcMainScorer.%suser.fieldname=src_machine_cluster", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcReductingScorer.output.field.name=srcReductingScore", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcReductingScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcReductingScorer.model.name=%sSrcComputer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcReductingScorer.%sDstComputer.context.fieldname=normalized_src_machine", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.srcReductingScorer.%sDstComputer.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
			}

			if(actionTypeToScoreFlag)
			{
				line = String.format("%s.%s_%s.score.actionTypeScorer.output.field.name=action_type_score", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.actionTypeScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.actionTypeScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.actionTypeScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.actionTypeScorer.%suser.fieldname=action_type", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);

			}

			//for each additional score field configure his basic scorer
			for (Map.Entry<String,String> entry : additionalScoreFeldsMap.entrySet()){

				String scoreFiledName = entry.getKey();
				String additionalFileForModel = additionalFiledToScoreFieldMap.get(scoreFiledName);


				line = String.format("%s.%s_%s.score.%sScorer.output.field.name=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, additionalFileForModel,scoreFiledName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.%sScorer.scorer=model-scorer", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, additionalFileForModel);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.%sScorer.model.name=%suser", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, additionalFileForModel,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.%sScorer.%suser.context.fieldname=${impala.table.fields.normalized.username}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, additionalFileForModel,dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				line = String.format("%s.%s_%s.score.%sScorer.%suser.fieldname=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, additionalFileForModel,dataSourceName,additionalFileForModel);
				writeLineToFile(line, fileWriterToConfigure, true);
			}




			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# SKIP - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);


			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("#  %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);


			line = String.format("#%s.%s_%s.skip.score=false", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("#%s.%s_%s.skip.model=false", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
			writeLineToFile(line, fileWriterToConfigure, true);

			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);
			writeLineToFile(String.format("# Stores - Key-value storage - for %s ",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("##############################################################################################################################", fileWriterToConfigure, true);

			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);
			writeLineToFile(String.format("#  %s",dataSourceName), fileWriterToConfigure, true);
			writeLineToFile("#--------------------------------------------------------------------------------------------------------------------------", fileWriterToConfigure, true);


			line = String.format("stores.%s-prevalence-stats.factory=org.apache.samza.storage.kv.KeyValueStorageEngineFactory",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.changelog=kafka.ssh-prevalence-stats-changelog",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.key.serde=string",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.msg.serde=jsonmodel",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);

			line = String.format("# This property is set to the number of key/value pairs that should be kept in this in-memory buffer, per task instance. The number cannot be greater than stores.*.object.cache.size.");
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.write.batch.size=1000",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("# This property determines the number of objects to keep in Samza's cache, per task instance. This same cache is also used for write buffering (see stores.*.write.batch.size). A value of 0 disables all caching and batching.");
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.object.cache.size=5000",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("# The size of LevelDB's block cache in bytes, per container. Note that this is an off-heap memory allocation, so the container's total memory use is the maximum JVM heap size plus the size of this cache.");
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.container.cache.size.bytes=104857600",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("# The amount of memory (in bytes) that LevelDB uses for buffering writes before they are written to disk,");
			writeLineToFile(line, fileWriterToConfigure, true);
			line = String.format("stores.%s-prevalence-stats.container.write.buffer.size.bytes=33554432",dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);


			fileWriterToConfigure.flush();
			affectedConfigList.add(fileToConfigure.getAbsolutePath());

		} catch (Exception e) {
			logger.error("There was an exception during execution - {} ", e);
			return false;

		}

		return true;

	}


}
