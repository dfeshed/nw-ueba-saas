package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationService;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by idanp on 12/20/2015.
 */
public class InitPartConfiguration extends ConfigurationService {

	private String secondFileToConfigurePath; //Will represent the streaming overriding file
	private File secondFileToConfigure;
	private FileWriter secondFileWriterToConfigure;

    public InitPartConfiguration()
	{
		logger = LoggerFactory.getLogger(InitPartConfiguration.class);
		this.fileToConfigurePath = root+"/fortscale/fortscale-core/fortscale/fortscale-collection/target/resources/fortscale-collection-overriding.properties";
		this.secondFileToConfigurePath = root+"/fortscale/streaming/config/fortscale-overriding-streaming.properties";
	}

	@Override
	public boolean init() {
		Boolean result;
		try {
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			this.secondFileToConfigure = new File(this.secondFileToConfigurePath);
			this.secondFileWriterToConfigure = new FileWriter(this.secondFileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during InitPartConfiguration init part execution - {} ", e.getMessage());
			System.out.println("There was an exception during execution please see more info at the log ");
			result = false;
		}

		return result;
	}

	@Override
	public  boolean applyConfiguration() throws Exception{

        try {
            String line = "";

            String dataSourceName = gdsConfigurationState.getDataSourceName();

			String dataSourceList  = gdsConfigurationState.getExistingDataSources();

			boolean hasSourceIp = gdsConfigurationState.getGDSSchemaDefinitionState().hasSourceIp();

			boolean hasTargetIp = gdsConfigurationState.getGDSSchemaDefinitionState().hasTargetIp();

			String dataFields = gdsConfigurationState.getGDSSchemaDefinitionState().getDataFields();

			String enrichFields = gdsConfigurationState.getGDSSchemaDefinitionState().getEnrichFields();

			String enrichDelimiter = gdsConfigurationState.getGDSSchemaDefinitionState().getEnrichDelimiter();

			String enrichTableName = gdsConfigurationState.getGDSSchemaDefinitionState().getEnrichTableName();

			String scoreFields = gdsConfigurationState.getGDSSchemaDefinitionState().getScoreFields();

			String scoreDelimiter = gdsConfigurationState.getGDSSchemaDefinitionState().getScoreDelimiter();

			String scoreTableName = gdsConfigurationState.getGDSSchemaDefinitionState().getScoreTableName();

			boolean hasTopSchema = gdsConfigurationState.getGDSSchemaDefinitionState().hasTopSchema();

			String normalizedUserNameField = gdsConfigurationState.getGDSSchemaDefinitionState().getNormalizedUserNameField();

			String dataDelimiter = gdsConfigurationState.getGDSSchemaDefinitionState().getDataDelimiter();

			String dataTableName = gdsConfigurationState.getGDSSchemaDefinitionState().getDataTableName();

            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", secondFileWriterToConfigure, true);
            writeLineToFile("\n", secondFileWriterToConfigure, true);

            line = "########################################### New Configuration For Generic Data Source  ########################################################";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //Configure the data source list
            line = String.format("fortscale.data.source=%s,%s",dataSourceList, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("########################################### %s ########################################################", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            line = String.format("impala.data.%s.table.field.username=username", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            if (hasSourceIp) {
                line = String.format("impala.data.%s.table.field.source=source_ip", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.source_name=hostname", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.src_class=src_class", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.normalized_src_machine=normalized_src_machine", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);
            }

            if (hasTargetIp) {
                line = String.format("impala.data.%s.table.field.target=target_ip", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.target_name=target", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.dst_class=dst_class", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.data.%s.table.field.normalized_dst_machine=normalized_dst_machine", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);
            }

            line = String.format("impala.data.%s.table.field.epochtime=date_time_unix", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

			line = String.format("impala.data.%s.table.field.normalized_username=%s",dataSourceName,normalizedUserNameField);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

            line = "########### Data Schema";
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.%s.have.data=true", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.data.%s.table.delimiter=%s", dataSourceName,dataDelimiter );
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.data.%s.table.name=%s", dataSourceName, dataTableName);
            writeLineToFile(line, fileWriterToConfigure, true);


            //Static configuration
            //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
            //hdfs paths
            line = String.format("hdfs.user.data.%s.path=${hdfs.user.data.path}/%s", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);


            //hdfs retention
            line = String.format("hdfs.user.data.%s.retention=90", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //is sensitive machien field
            line = String.format("impala.data.%s.table.field.is_sensitive_machine=is_sensitive_machine", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //partition type
            line = String.format("impala.data.%s.table.partition.type=monthly", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.data.%s.table.fields=%s", dataSourceName,dataFields );
            writeLineToFile(line, fileWriterToConfigure, true);


            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", secondFileWriterToConfigure, true);


            //Enrich fields
            line = "########### Enrich Schema";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            line = String.format("impala.%s.have.enrich=true", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.enricheddata.%s.table.fields=%s", dataSourceName,enrichFields );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            line = String.format("impala.enricheddata.%s.table.delimiter=%s", dataSourceName,enrichDelimiter );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            line = String.format("impala.enricheddata.%s.table.name=%s", dataSourceName, enrichTableName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
            //hdfs path
            line = String.format("hdfs.user.enricheddata.%s.path=${hdfs.user.enricheddata.path}/%s", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //hdfs retention
            line = String.format("hdfs.user.enricheddata.%s.retention=90", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //hdfs file name
            line = String.format("hdfs.enricheddata.%s.file.name=${impala.enricheddata.%s.table.name}.csv", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //partition strategy
            line = String.format("impala.enricheddata.%s.table.partition.type=daily", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //Score
            line = "########### Score Schema";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);


            //fields
            line = String.format("impala.score.%s.table.fields=%s", dataSourceName,scoreFields );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            line = String.format("impala.score.%s.table.delimiter=%s", dataSourceName,scoreDelimiter);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);


            line = String.format("impala.score.%s.table.name=%s", dataSourceName,scoreTableName );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
            //hdfs path
            line = String.format("hdfs.user.processeddata.%s.path=${hdfs.user.processeddata.path}/%s", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

            //hdfs retention
            line = String.format("hdfs.user.processeddata.%s.retention=90", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);


            //partition strategy
            line = String.format("impala.score.%s.table.partition.type=daily", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, secondFileWriterToConfigure, true);

			//hdfs file name
			line = String.format("{hdfs.user.processeddata.%s.file.name=${impala.processeddata.%s.table.name}.csv", dataSourceName, dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, secondFileWriterToConfigure, true);

            //Top Score schema
            if (hasTopSchema) {
                line = "########### Top Score Schema";
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.%s.have.topScore=true", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);


                //fields
                line = String.format("impala.score.%s.top.table.fields=%s", dataSourceName,scoreFields );
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                line = String.format("impala.score.%s.top.table.delimiter=%s", dataSourceName, scoreDelimiter);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);


                line = String.format("impala.score.%s.top.table.name=%s", dataSourceName, scoreTableName+"_top");
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

				//hdfs file name
				line = String.format("{hdfs.user.processeddata.%s.file.name=${impala.score.%s.top.table.name}.csv", dataSourceName, dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				writeLineToFile(line, secondFileWriterToConfigure, true);


                //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
                //hdfs path
                line = String.format("hdfs.user.processeddata.%s.top.path=${hdfs.user.processeddata.path}/%s_top", dataSourceName, dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

                //hdfs retention
                line = String.format("hdfs.user.processeddata.%s.top.retention=90", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);


                //partition strategy
                line = String.format("impala.score.%s.top.table.partition.type=daily", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, secondFileWriterToConfigure, true);

            }

            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", fileWriterToConfigure, true);

            line = String.format("%s.EventsJoiner.ttl=86400", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("kafka.%s.message.record.field.data_source=data_source", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("kafka.%s.message.record.field.last_state=last_state", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("kafka.%s.message.record.fields = ${impala.data.%s.table.fields},${kafka.%s.message.record.field.data_source},${kafka.%s.message.record.field.last_state}", dataSourceName, dataSourceName, dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = "read" + dataSourceName.toUpperCase() + ".morphline=file:resources/conf-files/parse" + dataSourceName.toUpperCase() + ".conf";
            writeLineToFile(line, fileWriterToConfigure, true);

			line = "enrich" + dataSourceName.toUpperCase() + ".morphline=file:resources/conf-files/enrichment/read" + dataSourceName.toUpperCase() + "_enrich.conf";
			writeLineToFile(line, fileWriterToConfigure, true);

            secondFileWriterToConfigure.flush();
            fileWriterToConfigure.flush();
        }
        catch(Exception e){
            logger.error("There was an exception during execution - {} ",e.getMessage()!=null ? e.getMessage() : e.getCause().getMessage());
            return false;
        }

		return  true;
	}

    @Override
	public boolean done() {
		if (secondFileWriterToConfigure != null) {
			try {
				secondFileWriterToConfigure.close();
			} catch (IOException exception) {
				logger.error("There was an exception during the file - {} closing  , cause - {} ", secondFileToConfigure.getName(), exception.getMessage());
				System.out.println("There was an exception during execution please see more info at the log ");
				return false;
			}
		}

		return super.done();
	}
}
