package fortscale.streaming.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.split.FileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;

/**
 * Service for hdfs and impala related operations
 */
public class HdfsService {

	private static Logger logger = LoggerFactory.getLogger(HdfsService.class);
	
	private HDFSPartitionsWriter appender;
	private String tableName;
	private String fileName;
	private ImpalaClient impalaClient;
	
	public HdfsService(String hdfsRootPath, String fileName, PartitionStrategy partition, FileSplitStrategy split, String tableName) throws IOException {
		// create hdfs appender
		this.fileName = fileName;
		appender = new HDFSPartitionsWriter(hdfsRootPath, partition, split);
		appender.open(fileName);
		
		// create impala client
		this.tableName = tableName;
		impalaClient = SpringService.getInstance().resolve(ImpalaClient.class);
	}
	
	public void writeLineToHdfs(String line, long timestamp) throws IOException {
		appender.writeLine(line, timestamp);
	}
	
	public void flushHdfs() throws Exception {
		appender.flush();
				
		Exception firstException = null;
		// add new partitions to impala
		for (String partition : appender.getNewPartitions()) {
			try {
				impalaClient.addPartitionToTable(tableName, partition);
			} catch (Exception e) {
				logger.error("error adding partition " + partition + " to table " + tableName, e);
				if (firstException==null)
					firstException = e;
			}
		}
		// clear the new partitions list from the hdfs appender, once they were added to impala
		appender.clearNewPartitions();
	
		// since for some reason when the FileSystem in HDFS appender is always open, 
		// the name node does not recognize changes in file up until we close the connection
		// so the work around to to close and re-open the connection here
		appender.close();
		appender.open(fileName);
		
		// refresh the impala table with new partitions data
		try {
			impalaClient.refreshTable(tableName);
		} catch (Exception e) {
			logger.error("error refreshing table " + tableName, e);
			if (firstException==null)
				firstException = e;
		}
		
		
		if (firstException!=null)
			throw firstException;
	}
	
	public void close() throws Exception {
		// call flush to ensure that all partitions were added to impala
		flushHdfs();
		appender.close();
	}
	
	
	
}
