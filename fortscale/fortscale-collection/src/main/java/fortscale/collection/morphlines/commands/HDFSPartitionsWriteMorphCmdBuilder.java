package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.MorphlineConfigService;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.hdfs.split.FileSplitUtils;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Configurable(preConstruction=true)
public class HDFSPartitionsWriteMorphCmdBuilder implements CommandBuilder{
	private static Logger logger = LoggerFactory.getLogger(HDFSPartitionsWriteMorphCmdBuilder.class);

	@Autowired
	private ImpalaClient impalaClient;
	
	@Autowired 
	private MorphlineConfigService morphlineConfigService;
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("HDFSPartitionsWrite");
	}

	@Autowired
	public StatsService statsService;

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		try {
			return new HDFSPartitionsWrite(this, config, parent, child, context);
		} catch (Exception e) {
			logger.error("got an exception while trying to create the command HDFSPartitionsWrite", e);
		}
		return null;
	}

	
	private class HDFSPartitionsWrite extends AbstractCommand {

		private MorphlineCommandMonitoringHelper commandMonitoringHelper = new MorphlineCommandMonitoringHelper();
		
		private String timestampField;
		private String hadoopPath;
		private String hadoopFilename;
		private String impalaTableName;
		private PartitionStrategy partitionStrategy;
		private FileSplitStrategy fileSplitStrategy;
		
		protected HDFSPartitionsWriter appender;
		protected RecordToStringItemsProcessor recordToString;

		String outputSeparator;

		public HDFSPartitionsWrite(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) throws PropertyNotExistException, IllegalStructuredProperty, IOException {
			super(builder, config, parent, child, context);
			
			try{
				this.timestampField = getStringValue(config, "timestampField");
				this.hadoopPath = getStringValue(config, "hadoopPath");
				this.hadoopFilename = getStringValue(config, "hadoopFilename");
				this.impalaTableName = getStringValue(config, "impalaTableName");
				String partitionType = getStringValue(config, "partitionType");
				this.partitionStrategy = PartitionsUtils.getPartitionStrategy(partitionType);
				String fileSplitStrategyType = getStringValue(config, "fileSplitStrategyType");
				this.fileSplitStrategy = FileSplitUtils.getFileSplitStrategy(fileSplitStrategyType);
				
				// build record to items processor
				String outputFields = getStringValue(config, "outputFields");
				outputSeparator = getStringValue(config, "outputSeparator");
				recordToString = new RecordToStringItemsProcessor(outputSeparator,statsService,"HDFSPartitionsWriteMorphCmdBuilder", ImpalaParser.getTableFieldNamesAsArray(outputFields));
				
				createOutputAppender();
			} catch(Exception e){
				logger.error("got the following exception while try to create the morphline command HDFSPartitionsWrite", e);
				if(appender != null){
					appender.close();
				}
			}
			
			validateArguments();
		}
		
		private String getStringValue(Config config, String path) throws PropertyNotExistException, IllegalStructuredProperty{
			return morphlineConfigService.getStringValue(getConfigs(), config, path);
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			//The specific Morphline metric
			MorphlineMetrics morphlineMetrics = commandMonitoringHelper.getMorphlineMetrics(inputRecord);

			if(appender == null){
				return super.doProcess(inputRecord);
			}
			String output = recordToString.process(inputRecord);
			
			// append to hadoop, if there is data to be written
			if (output!=null) {
				Long timestamp = RecordExtensions.getLongValue(inputRecord, timestampField);
				try {
					morphlineMetrics.writtenToHdfs++;
					appender.writeLine(output, timestamp.longValue());
				} catch (IOException e) {
					morphlineMetrics.errorWritingToHdfs++;
					logger.error("got an exception in HDFSPartitionsWrite command", e);
				}
			}

			return super.doProcess(inputRecord);

		}
		
		private void createOutputAppender() throws IOException {
			try {
				logger.debug("initializing hadoop appender in {}", hadoopPath);

				// calculate file directory path according to partition strategy
				appender = new HDFSPartitionsWriter(hadoopPath, partitionStrategy, fileSplitStrategy,outputSeparator);
				appender.open(hadoopFilename);

			} catch (IOException e) {
				String msg = String.format("error creating hdfs partitions writer at %s", hadoopPath);
				logger.error(msg, e);
				throw e;
			}
		}
		
		@Override
		protected void doNotify(Record notification) {
			for (Object event : Notifications.getLifecycleEvents(notification)) {
				if (event == Notifications.LifecycleEvent.SHUTDOWN && appender!=null) {
					try {
						closeOutputAppender();
						refreshImpala();
					} catch (IOException e) {
						logger.error("error closing appender", e);
					}
					appender = null;
				}
			}
			super.doNotify(notification);
		}
		
		private void closeOutputAppender() throws IOException {
			try {
				if(appender != null){
					logger.debug("flushing hdfs paritions at {}", hadoopPath);
					appender.close();
				}
			} catch (IOException e) {
				String msg = String.format("error closing hdfs partitions writer at %s", hadoopPath);
				logger.error(msg, e);
				throw e;
			}
		}
		
		protected void refreshImpala(){
			if(impalaClient == null){
				logger.error("impalaClient is null in command {}", HDFSPartitionsWrite.class);
				return;
			}
			if(appender == null){
				logger.error("appender is null in command {}", HDFSPartitionsWrite.class);
				return;
			}

			List<Exception> exceptions = new LinkedList<Exception>();
			
			// declare new partitions for impala
			for (String partition : appender.getNewPartitions()) {
				try {
					impalaClient.addPartitionToTable(impalaTableName, partition); 
				} catch (Exception e) {
					exceptions.add(e);
				}
			}
			appender.clearNewPartitions();
			
			try {
				impalaClient.refreshTable(impalaTableName);
			} catch (Exception e) {
				exceptions.add(e);
			}
			
			// log all errors if any
			for (Exception e : exceptions) {
				logger.error("error refreshing impala", e);
			}
		}
	}
}
