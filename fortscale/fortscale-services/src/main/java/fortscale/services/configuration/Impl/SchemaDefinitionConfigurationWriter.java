package fortscale.services.configuration.Impl;

import fortscale.services.configuration.ConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSSchemaDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Schema definition configuration writer implementation
 *
 * Created by idanp on 12/20/2015.
 */
public class SchemaDefinitionConfigurationWriter extends ConfigurationWriterService {

    private static final String COLLECTION_OVERRIDING_CONFIG_FILE_RELATIVE_PATH = "/fortscale/fortscale-core/fortscale/fortscale-collection/target/resources/fortscale-collection-overriding.properties";
    private static final String STREAMING_OVERRIDING_CONFIG_FILE_RELATIVE_PATH = "/fortscale/streaming/config/fortscale-overriding-streaming.properties";

    private String streamingOverridingFilePath;
	private File streamingOverridingFile;
	private FileWriter streamingOverridingFileWriter;

    public SchemaDefinitionConfigurationWriter()
	{
		logger = LoggerFactory.getLogger(SchemaDefinitionConfigurationWriter.class);
		this.fileToConfigurePath = USER_HOME_DIR + COLLECTION_OVERRIDING_CONFIG_FILE_RELATIVE_PATH;
		this.streamingOverridingFilePath = USER_HOME_DIR + STREAMING_OVERRIDING_CONFIG_FILE_RELATIVE_PATH;
	}

	@Override
	public boolean init() {
		Boolean result;
		try {
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			this.streamingOverridingFile = new File(this.streamingOverridingFilePath);
			this.streamingOverridingFileWriter = new FileWriter(this.streamingOverridingFile, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during SchemaDefinitionConfigurationWriter init part execution - {} ", e.getMessage());
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

            GDSSchemaDefinitionState schemaDefinitionState = gdsConfigurationState.getSchemaDefinitionState();

            boolean hasSourceIp = schemaDefinitionState.hasSourceIp();

			boolean hasTargetIp = schemaDefinitionState.hasTargetIp();

			String dataFields = schemaDefinitionState.getDataFields();

			String enrichFields = schemaDefinitionState.getEnrichFields();

			String enrichDelimiter = schemaDefinitionState.getEnrichDelimiter();

			String enrichTableName = schemaDefinitionState.getEnrichTableName();

			String scoreFields = schemaDefinitionState.getScoreFields();

			String scoreDelimiter = schemaDefinitionState.getScoreDelimiter();

			String scoreTableName = schemaDefinitionState.getScoreTableName();

			boolean hasTopSchema = schemaDefinitionState.hasTopSchema();

			String normalizedUserNameField = schemaDefinitionState.getNormalizedUserNameField();

			String dataDelimiter = schemaDefinitionState.getDataDelimiter();

			String dataTableName = schemaDefinitionState.getDataTableName();

            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", streamingOverridingFileWriter, true);
            writeLineToFile("\n", streamingOverridingFileWriter, true);

            line = "########################################### New Configuration For Generic Data Source  ########################################################";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //Configure the data source list
            line = String.format("fortscale.data.source=%s,%s",dataSourceList, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("########################################### %s ########################################################", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            line = String.format("impala.data.%s.table.field.username=username", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            if (hasSourceIp) {
                line = String.format("impala.data.%s.table.field.source=source_ip", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.source_name=hostname", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.src_class=src_class", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.normalized_src_machine=normalized_src_machine", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);
            }

            if (hasTargetIp) {
                line = String.format("impala.data.%s.table.field.target=target_ip", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.target_name=target", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.dst_class=dst_class", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.data.%s.table.field.normalized_dst_machine=normalized_dst_machine", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);
            }

            line = String.format("impala.data.%s.table.field.epochtime=date_time_unix", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

			line = String.format("impala.data.%s.table.field.normalized_username=%s",dataSourceName,normalizedUserNameField);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, streamingOverridingFileWriter, true);

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
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //partition type
            line = String.format("impala.data.%s.table.partition.type=monthly", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.data.%s.table.fields=%s", dataSourceName,dataFields );
            writeLineToFile(line, fileWriterToConfigure, true);


            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("\n", streamingOverridingFileWriter, true);


            //Enrich fields
            line = "########### Enrich Schema";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            line = String.format("impala.%s.have.enrich=true", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            line = String.format("impala.enricheddata.%s.table.fields=%s", dataSourceName,enrichFields );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            line = String.format("impala.enricheddata.%s.table.delimiter=%s", dataSourceName,enrichDelimiter );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            line = String.format("impala.enricheddata.%s.table.name=%s", dataSourceName, enrichTableName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
            //hdfs path
            line = String.format("hdfs.user.enricheddata.%s.path=${hdfs.user.enricheddata.path}/%s", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //hdfs retention
            line = String.format("hdfs.user.enricheddata.%s.retention=90", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //hdfs file name
            line = String.format("hdfs.enricheddata.%s.file.name=${impala.enricheddata.%s.table.name}.csv", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //partition strategy
            line = String.format("impala.enricheddata.%s.table.partition.type=daily", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //Score
            line = "########### Score Schema";
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);


            //fields
            line = String.format("impala.score.%s.table.fields=%s", dataSourceName,scoreFields );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            line = String.format("impala.score.%s.table.delimiter=%s", dataSourceName,scoreDelimiter);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);


            line = String.format("impala.score.%s.table.name=%s", dataSourceName,scoreTableName );
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
            //hdfs path
            line = String.format("hdfs.user.processeddata.%s.path=${hdfs.user.processeddata.path}/%s", dataSourceName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

            //hdfs retention
            line = String.format("hdfs.user.processeddata.%s.retention=90", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);


            //partition strategy
            line = String.format("impala.score.%s.table.partition.type=daily", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            writeLineToFile(line, streamingOverridingFileWriter, true);

			//hdfs file name
			line = String.format("{hdfs.user.processeddata.%s.file.name=${impala.processeddata.%s.table.name}.csv", dataSourceName, dataSourceName);
			writeLineToFile(line, fileWriterToConfigure, true);
			writeLineToFile(line, streamingOverridingFileWriter, true);

            //Top Score schema
            if (hasTopSchema) {
                line = "########### Top Score Schema";
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.%s.have.topScore=true", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);


                //fields
                line = String.format("impala.score.%s.top.table.fields=%s", dataSourceName,scoreFields );
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                line = String.format("impala.score.%s.top.table.delimiter=%s", dataSourceName, scoreDelimiter);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);


                line = String.format("impala.score.%s.top.table.name=%s", dataSourceName, scoreTableName+"_top");
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

				//hdfs file name
				line = String.format("{hdfs.user.processeddata.%s.file.name=${impala.score.%s.top.table.name}.csv", dataSourceName, dataSourceName);
				writeLineToFile(line, fileWriterToConfigure, true);
				writeLineToFile(line, streamingOverridingFileWriter, true);


                //TODO - DOES WE NEED TO PUT IT OUT TO BE DYNAMIC??
                //hdfs path
                line = String.format("hdfs.user.processeddata.%s.top.path=${hdfs.user.processeddata.path}/%s_top", dataSourceName, dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

                //hdfs retention
                line = String.format("hdfs.user.processeddata.%s.top.retention=90", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);


                //partition strategy
                line = String.format("impala.score.%s.top.table.partition.type=daily", dataSourceName);
                writeLineToFile(line, fileWriterToConfigure, true);
                writeLineToFile(line, streamingOverridingFileWriter, true);

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

            streamingOverridingFileWriter.flush();
            affectedConfigList.add(streamingOverridingFile.getAbsolutePath());

            fileWriterToConfigure.flush();
            affectedConfigList.add(fileToConfigure.getAbsolutePath());
        }
        catch(Exception e){
            logger.error("There was an exception during execution - {} ",e.getMessage()!=null ? e.getMessage() : e.getCause().getMessage());
            return false;
        }

		return  true;
	}

    @Override
	public boolean done() {
		if (streamingOverridingFileWriter != null) {
			try {
				streamingOverridingFileWriter.close();
			} catch (IOException exception) {
				logger.error("There was an exception during the file - {} closing  , cause - {} ", streamingOverridingFile.getName(), exception.getMessage());
				System.out.println("There was an exception during execution please see more info at the log ");
				return false;
			}
		}

		return super.done();
	}

    @Override
    public Set<String> getAffectedConfigList() {
        return affectedConfigList;
    }
}
