package fortscale.services.configuration.Impl;

import fortscale.services.configuration.StreamingConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * Implementation of Geo-location task configuration writer
 *
 * Created by idanp on 12/21/2015.
 */
public class HDFSWriteTaskConfigurationWriter extends StreamingConfigurationWriterService {

	public HDFSWriteTaskConfigurationWriter()
	{

		logger = LoggerFactory.getLogger(HDFSWriteTaskConfigurationWriter.class);
	}

	@Override
	public boolean init() {
		super.init();
		Boolean result;
		try {
			this.fileToConfigurePath = FORTSCALE_STREAMING_DIR_PATH + "hdfs-events-writer-task.properties";
			this.fileToConfigure = new File(this.fileToConfigurePath);
			this.fileWriterToConfigure = new FileWriter(this.fileToConfigure, true);
			result = true;
		} catch (Exception e) {
			logger.error("There was an exception during UserNormalizationTaskConfigurationWriter init part execution - {} ", e.getMessage());
			System.out.println(String.format("There was an exception during execution please see more info at the log "));
			result = false;

		}
		return result;

	}

	@Override
	public boolean applyConfiguration() throws Exception {
        try {
            String line;
            GDSEnrichmentDefinitionState.HDFSWriterState hdfsWriterState = gdsConfigurationState.getEnrichmentDefinitionState().getHdfsWriterState();

            fileWriterToConfigure.write("\n");
			fileWriterToConfigure.write("\n");

            String taskName = hdfsWriterState.getTaskName();

            writeMandatoryConfiguration(taskName, hdfsWriterState.getLastState(), hdfsWriterState.getOutputTopic(), hdfsWriterState.getOutputTopicEntry(), true);

            //bdp routing value
            line = String.format("%s.%s_%s.bdp.output.topics=", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //time stamp field
            line = String.format("%s.%s_%s.timestamp.field=${impala.data.%s.table.field.epochtime}", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //normalized_username
            line = String.format("%s.%s_%s.username.field=normalized_username", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //enrich fields
            line = String.format("%s.%s_%s.fields=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getFieldList());
            writeLineToFile(line, fileWriterToConfigure, true);

            //seperator fields
            line = String.format("%s.%s_%s.separator=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getDelimiter());
            writeLineToFile(line, fileWriterToConfigure, true);

            //hdfs path
            line = String.format("%s.%s_%s.hdfs.root=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getHdfsPath());
            writeLineToFile(line, fileWriterToConfigure, true);

            //file name
            line = String.format("%s.%s_%s.file.name=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getFileName());
            writeLineToFile(line, fileWriterToConfigure, true);

            //table name  fields
            line = String.format("%s.%s_%s.table.name=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getTableName());
            writeLineToFile(line, fileWriterToConfigure, true);

            //partition strategy fields
            line = String.format("%s.%s_%s.partition.strategy=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getPartitionStrategy());
            writeLineToFile(line, fileWriterToConfigure, true);

            //split strategy fields
            line = String.format("%s.%s_%s.split.strategy=fortscale.utils.hdfs.split.DailyFileSplitStrategy", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);

            //discriminator
            line = String.format("%s.%s_%s.discriminator.fields=%s", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName, hdfsWriterState.getDiscriminatorsFields());
            writeLineToFile(line, fileWriterToConfigure, true);

            //flush buffer size
            writeLineToFile("# Buffer no more than 10000 events before flushing to HDFS", fileWriterToConfigure, true);
            line = String.format("%s.%s_%s.events.flush.threshold=10000", FORTSCALE_CONFIGURATION_PREFIX, dataSourceName, taskName);
            writeLineToFile(line, fileWriterToConfigure, true);


            //Key-Value store configuration

            line = String.format("stores.hdfs-write-%senrich.factory=org.apache.samza.storage.kv.RocksDbKeyValueStorageEngineFactory", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.changelog=kafka.hdfs-write-crmsfEnrich-changelog", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.key.serde=string", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.msg.serde=timebarrier", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = "# This property is set to the number of key/value pairs that should be kept in this in-memory buffer, per task instance. The number cannot be greater than stores.*.object.cache.size.";
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.write.batch.size=25", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = "# This property determines the number of objects to keep in Samza's cache, per task instance. This same cache is also used for write buffering (see stores.*.write.batch.size). A value of 0 disables all caching and batching.";
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.object.cache.size=100", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = "# The size of LevelDB's block cache in bytes, per container. Note that this is an off-heap memory allocation, so the container's total memory use is the maximum JVM heap size plus the size of this cache.";
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.container.cache.size.bytes=2000", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);
            line = "# The amount of memory (in bytes) that LevelDB uses for buffering writes before they are written to disk.";
            writeLineToFile(line, fileWriterToConfigure, true);
            line = String.format("stores.hdfs-write-%senrich.container.write.buffer.size.bytes=1000", dataSourceName);
            writeLineToFile(line, fileWriterToConfigure, true);


            writeLineToFile("\n", fileWriterToConfigure, true);
            writeLineToFile("#############", fileWriterToConfigure, true);

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
